package com.shimmermare.stuffiread.importer.ponyfiction

import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.StoryImporter
import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.cover.StoryCover
import com.shimmermare.stuffiread.stories.cover.StoryCoverFormat
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.util.AppHttpClient
import com.shimmermare.stuffiread.util.AppJson
import io.github.aakira.napier.Napier
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object PonyfictionImporter : StoryImporter<PonyfictionImportSettings> {
    private const val DOMAIN = "https://ponyfiction.org"
    private val REQUEST_DELAY: Duration = 500.milliseconds
    private val IMG_REGEX = Regex("<img.*?src=\"([^\"]+)\"")

    private var cachedMetaMappings: MetaMappings? = null

    override suspend fun import(settings: PonyfictionImportSettings): ImportedStory {
        return withContext(Dispatchers.IO) {
            val downloadResult = download(settings.storyId)
            val story: PonyfictionStory = downloadResult.story

            val author: String? = downloadResult.contributors.filter { it.isAuthor }.map { it.user }.let {
                if (it.isEmpty()) {
                    null
                } else {
                    retrieveAuthorNames(it)
                }
            }

            // Because jsonl dump has only tag and character ids and no names, we need to download meta dump
            // It is not small, so should be cached
            if (cachedMetaMappings == null) {
                cachedMetaMappings = downloadMetaMappings()
            }

            val tags: MutableSet<String> = hashSetOf()
            story.characters.forEach { characterId ->
                val tag = cachedMetaMappings!!.characterNamesById.getOrDefault(characterId, characterId.toString())
                tags.add(tag)
            }
            downloadResult.tags.forEach {
                val tagName = cachedMetaMappings!!.tagNamesById.getOrDefault(it.tag, it.tag.toString())
                tags.add(tagName)
            }

            val cover: StoryCover? = story.summary?.let { tryToFindAndDownloadCover(it) }

            val files: MutableList<StoryFile> = mutableListOf()
            if (settings.downloadFb2) {
                val fb2File = downloadFb2(settings.storyId, story)
                files.add(fb2File)
            }
            if (settings.downloadTxt) {
                downloadResult.chapters.sortedBy { it.order }.forEach { chapter ->
                    val txtFile = StoryFile.fromText(
                        chapter.title,
                        chapter.text,
                        added = chapter.firstPublishedAt ?: story.firstPublishedAt ?: Clock.System.now()
                    )
                    files.add(txtFile)
                }
            }

            ImportedStory(
                author = StoryAuthor.of(author),
                name = StoryName(downloadResult.story.title),
                url = StoryURL.of("$DOMAIN/story/${settings.storyId}"),
                description = StoryDescription.of(downloadResult.story.summary),
                published = downloadResult.story.firstPublishedAt,
                changed = downloadResult.story.updated,
                tags,
                cover,
                files,
            )
        }
    }

    private suspend fun download(storyId: PonyfictionStoryId): DownloadResult {
        val dumpUrl = "$DOMAIN/story/${storyId}_dump.jsonl"
        val response = AppHttpClient.get(dumpUrl).call.response
        require(response.status == HttpStatusCode.OK) {
            "Raw content request failed: ${response.status}"
        }

        var story: PonyfictionStory? = null
        val tags = mutableListOf<Storytag>()
        val contributors = mutableListOf<Storycontributor>()
        val chapters = mutableListOf<Chapter>()

        val body = response.bodyAsChannel()
        do {
            val line = body.readUTF8Line() ?: break
            val jsonObject = AppJson.parseToJsonElement(line) as? JsonObject ?: continue

            when (jsonObject["_entity"]?.jsonPrimitive?.contentOrNull) {
                "story" -> {
                    story = AppJson.decodeFromJsonElement<PonyfictionStory>(jsonObject)
                }

                "storytag" -> {
                    val tag = AppJson.decodeFromJsonElement<Storytag>(jsonObject)
                    tags.add(tag)
                }

                "storycontributor" -> {
                    val contributor = AppJson.decodeFromJsonElement<Storycontributor>(jsonObject)
                    contributors.add(contributor)
                }

                "chapter" -> {
                    val chapter = AppJson.decodeFromJsonElement<Chapter>(jsonObject)
                    chapters.add(chapter)
                }

                else -> {}
            }

        } while (!body.isClosedForRead)


        if (story == null) {
            throw Exception("Story entity not found in JSONL story dump")
        }
        return DownloadResult(story, tags, contributors, chapters)
    }

    private suspend fun retrieveAuthorNames(authorIds: List<UInt>): String {
        // The only way to get author names I found is to parse RSS feed

        return authorIds.mapIndexed { index, authorId ->
            // Add a slight delay to not trigger cloudflare captcha
            if (index > 0) {
                delay(REQUEST_DELAY)
            }

            val feedUrl = "$DOMAIN/feeds/accounts/$authorId"

            try {
                val response = AppHttpClient.get(feedUrl).call.response
                if (response.status != HttpStatusCode.OK) {
                    throw Exception("Failed to request author RSS feed $feedUrl: ${response.status}")
                }
                val feedText = response.bodyAsText(Charsets.UTF_8)
                PonyfictionAuthorNameProvider.getAuthorName(authorId, feedText)
            } catch (e: Exception) {
                Napier.i(e) { "Attempted to get author name by ID $authorId and failed" }
                "Failed to get name (ID: $authorId})"
            }
        }.joinToString(", ")
    }

    private suspend fun downloadMetaMappings(): MetaMappings {
        val dumpUrl = "$DOMAIN/media/mini_fiction_dump.zip"
        return try {
            val response = AppHttpClient.get(dumpUrl).call.response
            if (response.status != HttpStatusCode.OK) {
                throw Exception("Request failed: ${response.status}")
            }
            val bytes = response.readBytes()
            PonyfictionMetaDumpParser.getMetaMappings(bytes)
        } catch (e: Exception) {
            Napier.e(e) { "Failed to download meta dump, tags and characters won't be mapped" }
            MetaMappings(emptyMap(), emptyMap())
        }
    }

    private suspend fun tryToFindAndDownloadCover(summary: String): StoryCover? {
        // Find IMG tags in summary, if found - try to download the first one
        for (match in IMG_REGEX.findAll(summary)) {
            val imageUrl = match.groupValues[1]
            try {
                val response = AppHttpClient.get(imageUrl).call.response
                if (response.status != HttpStatusCode.OK) {
                    throw Exception("Request failed: ${response.status}")
                }
                val contentType: ContentType? = response.contentType()
                if (contentType?.contentType != "image") {
                    throw Exception("Content is not an image: $contentType")
                }

                val imageFormat: StoryCoverFormat = StoryCoverFormat.getByMimeSubType(contentType.contentSubtype)
                    ?: throw Exception("Unsupported image type: $contentType")

                val imageBytes = response.readBytes()
                Napier.d { "Downloaded story cover from $imageUrl - $imageFormat, ${imageBytes.size} bytes" }
                return StoryCover(format = imageFormat, data = imageBytes)
            } catch (e: Exception) {
                Napier.i(e) { "Attempted to get story cover from $imageUrl and failed" }
            }
        }
        return null
    }

    private suspend fun downloadFb2(storyId: PonyfictionStoryId, story: PonyfictionStory): StoryFile {
        val url = "$DOMAIN/story/${storyId}/download/${story.title}.fb2"
        val response = AppHttpClient.get(url).call.response
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to download FB2: ${response.status}")
        }
        val content = response.readBytes()
        return StoryFile(
            meta = StoryFileMeta(
                fileName = StoryFile.createFileName(story.title, StoryFileFormat.FB2),
                format = StoryFileFormat.FB2,
                originalName = story.title,
                added = story.firstPublishedAt ?: Clock.System.now(),
                wordCount = story.words,
                size = content.size.toUInt()
            ),
            content
        )
    }

    private data class DownloadResult(
        val story: PonyfictionStory,
        val tags: List<Storytag>,
        val contributors: List<Storycontributor>,
        val chapters: List<Chapter>,
    )

    @Serializable
    private data class PonyfictionStory(
        val title: String,
        val summary: String? = null,
        val characters: List<UInt> = emptyList(),
        @SerialName("first_published_at")
        val firstPublishedAt: Instant? = null,
        val updated: Instant? = null,
        val words: UInt = 0u,
    )

    @Serializable
    private data class Storytag(
        val tag: UInt,
    )

    @Serializable
    private data class Storycontributor(
        val user: UInt,
        @SerialName("is_author")
        val isAuthor: Boolean = false,
        @SerialName("is_editor")
        val isEditor: Boolean = false,
    )

    @Serializable
    private data class Chapter(
        val order: UInt,
        val title: String,
        @SerialName("first_published_at")
        val firstPublishedAt: Instant? = null,
        val updated: Instant? = null,
        val text: String,
        val words: UInt = 0u,
    )
}
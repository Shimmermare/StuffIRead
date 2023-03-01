package com.shimmermare.stuffiread.importer.pastebin

import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.StoryImporter
import com.shimmermare.stuffiread.stories.Story
import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.util.AppHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant


object PastebinImporter : StoryImporter<PastebinImportSettings> {
    override suspend fun import(settings: PastebinImportSettings): ImportedStory {
        return withContext(Dispatchers.IO) {
            val pastes = settings.pasteKeys
                .map { key ->
                    async {
                        try {
                            PastebinMetadataProvider.get(key)
                        } catch (e: Exception) {
                            throw RuntimeException("Failed to get paste metadata for $key", e)
                        }
                    }
                }
                .awaitAll()
                .map { meta ->
                    async {
                        val content = try {
                            getRawContent(meta.key)
                        } catch (e: Exception) {
                            throw RuntimeException("Failed to get paste content for ${meta.key}", e)
                        }
                        PastebinPaste(
                            key = meta.key,
                            author = meta.author,
                            name = meta.name,
                            addedDate = meta.addedDate,
                            content = content
                        )
                    }
                }
                .awaitAll()

            val files = pastes.map { StoryFile.fromText(it.name, it.content) }

            ImportedStory(
                story = Story(
                    // Use author of first paste
                    author = StoryAuthor.of(pastes.first().author),
                    name = if (pastes.size == 1) {
                        StoryName(pastes.first().name)
                    } else {
                        // If pastes have common prefix - use it as name. E.g. common case "%story name% - Part N"
                        pastes.zipWithNext()
                            .map { (first, second) -> first.name.commonPrefixWith(second.name) }
                            .minBy { it.length }
                            .trim(' ', '-', ':')
                            .let { StoryName(it.ifBlank { pastes.first().name }) }
                    },
                    url = StoryURL.of(pastes.first().key.toPastebinUrl()),
                    description = StringBuilder().apply {
                        append("Imported from Pastebin")
                        if (pastes.size > 1) {
                            append("\nConsists of multiple pastes:\n" + pastes.joinToString("\n") { it.key.toPastebinUrl() })
                        }
                    }.let { StoryDescription.of(it.toString()) },
                    // Oldest date for published
                    published = pastes.minOf { it.addedDate },
                    // Newest date for changed
                    changed = pastes.maxOf { it.addedDate },
                ),
                files = files
            )
        }
    }

    private suspend fun getRawContent(pasteKey: PasteKey): String {
        val response = AppHttpClient.get("https://pastebin.com/raw/${pasteKey.value}").call.response
        require(response.status == HttpStatusCode.OK) {
            "Raw content request failed: ${response.status}"
        }
        return response.bodyAsText(Charsets.UTF_8)
    }

    private fun PasteKey.toPastebinUrl(): String {
        return "https://pastebin.com/${value}"
    }
}

private data class PastebinPaste(
    val key: PasteKey,
    val author: String,
    val name: String,
    val addedDate: Instant,
    val content: String,
)

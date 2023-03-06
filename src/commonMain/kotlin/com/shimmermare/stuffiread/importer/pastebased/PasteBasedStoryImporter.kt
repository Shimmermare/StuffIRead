package com.shimmermare.stuffiread.importer.pastebased

import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.StoryImporter
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Common importer for Pastebin-like websites.
 *
 * How it works:
 * 1. Accept 1 or more paste IDs.
 * 2. Request metadata for each paste: author, name, tags, etc.
 * 3. Request content as UTF8 text for each paste.
 * 4. Map values:
 *   - [ImportedStory.author] is author of the first imported paste.
 *   - [ImportedStory.name] is the longest common prefix of imported pastes, or name of first paste if prefix length
 *     is less than half of first paste's name.
 *   - [ImportedStory.url] is URL of the first imported paste.
 *   - [ImportedStory.description] contains URLs of other pastes if there's more than one.
 *   - [ImportedStory.tags] merged tags from all pastes.
 *   - [ImportedStory.published] earliest added date of imported pastes.
 *   - [ImportedStory.changed] latest modified date of imported pastes.
 *   - [ImportedStory.files] paste content as TXT files.
 *
 * Importing story cover image is not supported.
 */
abstract class PasteBasedStoryImporter<PasteId>(
    val descriptionPrefix: String = "",
    val requestDelay: Duration = 500.milliseconds
) : StoryImporter<PasteImportSettings<PasteId>> {

    override suspend fun import(settings: PasteImportSettings<PasteId>): ImportedStory {
        val useDelay = settings.pasteIds.size > 1
        return withContext(Dispatchers.IO) {
            val pastes = settings.pasteIds
                .map { pasteId ->
                    try {
                        if (useDelay) delay(requestDelay)
                        requestMetadata(pasteId)
                    } catch (e: Exception) {
                        throw RuntimeException("Failed to get paste metadata for $pasteId", e)
                    }
                }
                .map { meta ->
                    val content = try {
                        if (useDelay) delay(requestDelay)
                        requestRawContent(meta.id)
                    } catch (e: Exception) {
                        throw RuntimeException("Failed to get paste content for ${meta.id}", e)
                    }
                    Paste(meta, content)
                }

            val files = pastes.map { StoryFile.fromText(it.meta.name, it.content) }

            ImportedStory(
                // Use author of first paste
                author = StoryAuthor.of(pastes.first().meta.author),
                name = if (pastes.size == 1) {
                    StoryName(pastes.first().meta.name)
                } else {
                    // If pastes have common prefix - use it as name. E.g. common case "%story name% - Part N"
                    pastes.zipWithNext()
                        .map { (first, second) -> first.meta.name.commonPrefixWith(second.meta.name) }
                        .minBy { it.length }
                        .trim(' ', '-', ':')
                        .let {
                            if (it.length < pastes.first().meta.name.length / 2) {
                                StoryName(pastes.first().meta.name)
                            } else {
                                StoryName(it)
                            }
                        }
                },
                url = StoryURL.of(getPasteUrl(pastes.first().meta.id)),
                description = StringBuilder().apply {
                    append(descriptionPrefix)
                    if (pastes.size > 1) {
                        append("\nConsists of multiple pastes:\n" + pastes.joinToString("\n") { getPasteUrl(it.meta.id) })
                    }
                }.let { StoryDescription.of(it.toString()) },
                tags = pastes.flatMapTo(mutableSetOf()) { it.meta.tags },
                // Oldest date for published
                published = pastes.mapNotNull { it.meta.addedDate }.minOrNull(),
                // Newest date for changed
                changed = pastes.mapNotNull { it.meta.modifiedDate }.maxOrNull(),
                cover = null,
                files = files,
            )
        }
    }

    protected abstract suspend fun requestMetadata(pasteId: PasteId): PasteMetadata<PasteId>

    private suspend fun requestRawContent(pasteId: PasteId): String {
        val rawUrl = getRawUrl(pasteId)
        val response = AppHttpClient.get(rawUrl).call.response
        require(response.status == HttpStatusCode.OK) {
            "Raw content request failed: ${response.status}"
        }
        return response.bodyAsText(Charsets.UTF_8)
    }

    abstract fun getPasteUrl(pasteId: PasteId): String

    abstract fun getRawUrl(pasteId: PasteId): String
}

private data class Paste<PasteId>(
    val meta: PasteMetadata<PasteId>,
    val content: String
)
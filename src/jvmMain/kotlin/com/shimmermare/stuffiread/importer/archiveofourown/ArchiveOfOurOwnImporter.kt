package com.shimmermare.stuffiread.importer.archiveofourown

import com.shimmermare.stuffiread.importer.ImportedStory
import com.shimmermare.stuffiread.importer.StoryImporter
import com.shimmermare.stuffiread.importer.archiveofourown.ArchiveOfOurOwnImportSettings.FileType
import com.shimmermare.stuffiread.stories.StoryAuthor
import com.shimmermare.stuffiread.stories.StoryDescription
import com.shimmermare.stuffiread.stories.StoryName
import com.shimmermare.stuffiread.stories.StoryURL
import com.shimmermare.stuffiread.stories.file.StoryFile
import com.shimmermare.stuffiread.stories.file.StoryFileFormat
import com.shimmermare.stuffiread.stories.file.StoryFileMeta
import com.shimmermare.stuffiread.util.AppHttpClient
import io.github.aakira.napier.Napier
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDate
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

/**
 * WHY one of the most popular fanfiction websites doesn't have an API?!
 * Unfortunately only way to get story is to parse HTML.
 */
actual object ArchiveOfOurOwnImporter : StoryImporter<ArchiveOfOurOwnImportSettings> {
    private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val REQUEST_DELAY = 500.milliseconds

    actual override suspend fun import(settings: ArchiveOfOurOwnImportSettings): ImportedStory {
        val workId = settings.workId

        val meta = requestMeta(workId)

        val files = withContext(Dispatchers.IO) {
            requestFiles(workId, meta, settings.fileTypes)
        }

        return ImportedStory(
            author = StoryAuthor.of(meta.author),
            name = StoryName(meta.name),
            url = StoryURL.of(meta.url),
            description = StoryDescription.of(meta.description),
            published = meta.published,
            changed = meta.changed,
            tags = meta.tags,
            files = files,
        )
    }

    private fun requestMeta(workId: WorkId): Meta {
        val workUrl = "https://archiveofourown.org/works/$workId"

        val html = Jsoup.connect(workUrl)
            .followRedirects(true)
            .data("view_adult", "true")
            .get()

        val author = try {
            html.selectFirst("a[rel='author']")!!.text()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse author for $workId" }
            "Failed to get author"
        }

        val name = try {
            html.selectFirst("#workskin > .preface > .title.heading")!!.text()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse name for $workId" }
            "Failed to parse name"
        }

        val description = try {
            val builder = StringBuilder()

            html.selectFirst("#workskin > .preface > .summary > .userstuff")?.let {
                builder.append("Summary:\n")
                builder.append(it.wholeText().trim(' ', '\n'))
            }
            html.selectFirst("#workskin > .preface > .notes > .userstuff")?.let {
                if (builder.isNotBlank()) builder.append("\n\n")
                builder.append("Notes:\n")
                builder.append(it.wholeText().trim(' ', '\n'))
            }

            builder.toString().ifBlank { null }
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse description for $workId" }
            "Failed to parse description"
        }

        val published = try {
            val textDate = html.selectFirst(".work.meta > dd.stats > dl.stats > dd.published")!!.text()
            parseDate(textDate)
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse published date for $workId" }
            null
        }

        val changed = try {
            val textDate = html.selectFirst(".work.meta > dd.stats > dl.stats > dd.status")
            // Changed date can be missing
            if (textDate == null) {
                published
            } else {
                parseDate(textDate.text())
            }
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse changed date for $workId" }
            null
        }

        val tags: Set<String> = try {
            html.select(".work.meta > dd.tags > ul > li").map { it.text() }.toSet()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse tags for $workId" }
            emptySet()
        }

        val wordCount: UInt = try {
            html.selectFirst(".work.meta > dd.stats > dl.stats > dd.words")!!.text().toUInt()
        } catch (e: Exception) {
            Napier.e(e) { "Failed to parse word count for $workId" }
            0u
        }

        return Meta(
            author = author,
            name = name,
            url = workUrl,
            description = description,
            published = published,
            changed = changed,
            tags = tags,
            wordCount = wordCount
        )
    }

    private fun parseDate(dateText: String): Instant {
        return LocalDate.from(DATE_FORMAT.parse(dateText))
            .toKotlinLocalDate()
            .atTime(12, 0)
            .toInstant(TimeZone.currentSystemDefault())
    }

    private suspend fun requestFiles(workId: WorkId, meta: Meta, fileTypes: Set<FileType>): List<StoryFile> {
        val downloadEndpoint = "https://archiveofourown.org/downloads/$workId"

        return fileTypes.map { fileType ->
            val downloadUrl = "${downloadEndpoint}/file.${fileType.name.lowercase()}"

            val response = AppHttpClient.get(downloadUrl).call.response
            if (response.status != HttpStatusCode.OK) {
                error("Download of filetype $fileType for work ID $workId has failed: ${response.status}")
            }

            val contentDisposition = ContentDisposition.parse(response.headers["Content-Disposition"]!!)

            val content = response.bodyAsChannel().toByteArray()

            if (fileTypes.size > 1) delay(REQUEST_DELAY)

            StoryFile(
                meta = StoryFileMeta(
                    fileName = contentDisposition.parameter("filename")!!,
                    format = when (fileType) {
                        FileType.EPUB -> StoryFileFormat.EPUB
                        FileType.PDF -> StoryFileFormat.PDF
                        FileType.HTML -> StoryFileFormat.HTML
                    },
                    originalName = meta.name,
                    wordCount = meta.wordCount,
                    size = content.size.toUInt()
                ),
                content = content,
            )
        }
    }
}

private data class Meta(
    val author: String,
    val name: String,
    val url: String,
    val description: String?,
    val published: Instant?,
    val changed: Instant?,
    val tags: Set<String>,
    val wordCount: UInt,
)
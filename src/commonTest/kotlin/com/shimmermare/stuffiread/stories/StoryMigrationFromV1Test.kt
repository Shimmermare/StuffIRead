package com.shimmermare.stuffiread.stories

import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.util.repeat
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class StoryMigrationFromV1Test {
    @Test
    fun `No reads`() {
        val v1Json = buildV1Json(null, null, 0u)
        val expected = buildExpected(listOf())
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `1 read, only firstRead is known`() {
        val firstRead = Instant.fromEpochSeconds(1234567890)
        val v1Json = buildV1Json(firstRead, null, 1u)
        val expected = buildExpected(listOf(StoryRead(firstRead)))
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `1 read, only lastRead is known`() {
        val lastRead = Instant.fromEpochSeconds(1234567890)
        val v1Json = buildV1Json(null, lastRead, 1u)
        val expected = buildExpected(listOf(StoryRead(lastRead)))
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `1 read, no dates`() {
        val v1Json = buildV1Json(null, null, 1u)
        val expected = buildExpected(listOf(StoryRead(Instant.fromEpochSeconds(0))))
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `Multiple reads, only firstRead is known`() {
        val firstRead = Instant.fromEpochSeconds(1234567890)
        val v1Json = buildV1Json(firstRead, null, 9u)
        val expected = buildExpected(StoryRead(firstRead).repeat(9))
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `Multiple reads, only lastRead is known`() {
        val lastRead = Instant.fromEpochSeconds(1234567890)
        val v1Json = buildV1Json(null, lastRead, 9u)
        val expected = buildExpected(StoryRead(lastRead).repeat(9))
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `Multiple reads, firstRead and lastRead are known`() {
        val firstRead = Instant.fromEpochSeconds(111111111)
        val lastRead = Instant.fromEpochSeconds(999999999)
        val v1Json = buildV1Json(firstRead, lastRead, 9u)

        // Interpolation is expected
        val expected = buildExpected((1..9).map {
            StoryRead(Instant.fromEpochSeconds(111111111L * it))
        })

        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    @Test
    fun `Multiple reads, no dates`() {
        val v1Json = buildV1Json(null, null, 9u)
        val expected = buildExpected(StoryRead(Instant.fromEpochSeconds(0)).repeat(9))
        val migrated = Json.decodeFromString(StorySerializer, v1Json)
        assertEquals(expected, migrated)
    }

    private fun buildV1Json(firstRead: Instant?, lastRead: Instant?, timesRead: UInt): String {
        return """
            {
                "id": 1,
                "author": "Example Author",
                "name": "Example Story Name",
                "url": "https://example.com/1",
                "description": "Lorem ipsum dolor sit amet.",
                "published": "2023-01-01T00:00:00Z",
                "changed": "2023-02-02T00:00:00Z",
                "tags": [
                    1,
                    2,
                    3
                ],
                "sequels": [
                    2
                ],
                "score": 0.75,
                "review": "Lorem ipsum consectetur adipiscing elit.",
                "firstRead": ${Json.encodeToString(firstRead)},
                "lastRead": ${Json.encodeToString(lastRead)},
                "timesRead": ${timesRead},
                "created": "2023-03-03T00:00:00Z",
                "updated": "2023-04-04T00:00:00Z",
                "#version": 1
            }
        """.trimIndent()
    }

    private fun buildExpected(reads: List<StoryRead>): Story {
        return Story(
            id = StoryId(1u),
            author = StoryAuthor.of("Example Author"),
            name = StoryName("Example Story Name"),
            url = StoryURL.of("https://example.com/1"),
            description = StoryDescription.of("Lorem ipsum dolor sit amet."),
            published = Instant.fromEpochSeconds(1672531200),
            changed = Instant.fromEpochSeconds(1675296000),
            tags = setOf(TagId(1u), TagId(2u), TagId(3u)),
            sequels = setOf(StoryId(2u)),
            score = Score(0.75F),
            review = StoryReview.of("Lorem ipsum consectetur adipiscing elit."),
            reads = reads,
            created = Instant.fromEpochSeconds(1677801600),
            updated = Instant.fromEpochSeconds(1680566400),
        )
    }
}
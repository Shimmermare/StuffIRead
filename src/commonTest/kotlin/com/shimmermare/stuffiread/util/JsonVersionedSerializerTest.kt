package com.shimmermare.stuffiread.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonVersionedSerializerTest {
    @Test
    fun `Always includes version property`() {
        val serializer = JsonVersionedSerializer(
            currentVersion = 123u,
            migrations = listOf(
                Migration(123u) { it }
            ),
            actualSerializer = Example.serializer()
        )

        val json = Json.encodeToJsonElement(serializer, Example("value"))
        assertEquals(
            expected = JsonObject(
                mapOf("property" to JsonPrimitive("value"), "#version" to JsonPrimitive(123))
            ),
            actual = json
        )
    }

    @Test
    fun `Fails if migration to current version is missing`() {
        // Good: up to current version
        JsonVersionedSerializer(
            currentVersion = 2u,
            migrations = listOf(
                Migration(2u) { it }
            ),
            actualSerializer = Example.serializer()
        )

        // Bad: only up to previous version
        assertFailsWith(IllegalArgumentException::class) {
            JsonVersionedSerializer(
                currentVersion = 2u,
                migrations = listOf(
                    Migration(1u) { it }
                ),
                actualSerializer = Example.serializer()
            )
        }
    }

    @Test
    fun `Fails when provided migration to higher than current version`() {
        // Good: up to current version
        JsonVersionedSerializer(
            currentVersion = 2u,
            migrations = listOf(
                Migration(2u) { it }
            ),
            actualSerializer = Example.serializer()
        )

        // Bad: only up higher than current version
        assertFailsWith(IllegalArgumentException::class) {
            JsonVersionedSerializer(
                currentVersion = 2u,
                migrations = listOf(
                    Migration(3u) { it }
                ),
                actualSerializer = Example.serializer()
            )
        }
    }

    @Test
    fun `Applies migrations in order`() {
        val serializer = JsonVersionedSerializer(
            currentVersion = 2u,
            defaultVersion = 0u,
            migrations = listOf(
                Migration(1u) {
                    JsonObject(it.jsonObject + ("newProp" to JsonPrimitive("2")))
                },
                Migration(2u) {
                    val oldPropVal = it.jsonObject["oldProp"]?.jsonPrimitive?.content
                    val newPropVal = it.jsonObject["newProp"]?.jsonPrimitive?.content
                    JsonObject(mapOf("property" to JsonPrimitive("$oldPropVal$newPropVal")))
                }
            ),
            actualSerializer = Example.serializer()
        )
        val value = Json.decodeFromString(serializer, "{\"oldProp\": \"1\"}")

        assertEquals(Example("12"), value)
    }
}

@Serializable
private data class Example(
    val property: String
)
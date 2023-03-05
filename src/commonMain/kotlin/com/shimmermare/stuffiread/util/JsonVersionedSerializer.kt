package com.shimmermare.stuffiread.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.util.*

typealias Version = UInt

/**
 * Serializer that handles version migration.
 * Always adds current version to serialized value.
 */
open class JsonVersionedSerializer<T : Any>(
    private val versionProperty: String = "#version",
    private val currentVersion: Version,
    private val defaultVersion: Version = 0u,
    /**
     * List of migrations up to [currentVersion].
     */
    migrations: List<Migration>,
    /**
     * Serializer that is used for actual version.
     */
    actualSerializer: KSerializer<T>
) : JsonTransformingSerializer<T>(actualSerializer) {

    private val currentVersionProperty: Pair<String, JsonPrimitive> =
        versionProperty to JsonPrimitive(currentVersion.toLong())

    private val migrationsByTargetVersion: NavigableMap<Version, Migration> =
        migrations.associateByTo(TreeMap()) { it.targetVersion }

    init {
        require(currentVersion >= defaultVersion) {
            "Default version ($defaultVersion) can't be higher than current ($currentVersion)"
        }
        require(migrationsByTargetVersion.isEmpty() || migrationsByTargetVersion.containsKey(currentVersion)) {
            "Migration to current version $currentVersion is not provided"
        }
        require(migrationsByTargetVersion.isEmpty() || migrationsByTargetVersion.lastKey() <= currentVersion) {
            "Migration to version ${migrationsByTargetVersion.lastKey()} that is higher than current version $currentVersion is not allowed"
        }
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        return JsonObject(element.jsonObject + currentVersionProperty)
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) error("Not an object")

        val version: Version = element[versionProperty]?.jsonPrimitive?.longOrNull?.toUInt() ?: defaultVersion
        if (version == currentVersion) {
            return if (element.containsKey(versionProperty))
                JsonObject(element - versionProperty)
            else
                element
        }

        var result: JsonElement = JsonObject(element - versionProperty)
        migrationsByTargetVersion.tailMap(version, false).values.forEach { migration ->
            try {
                result = migration.transform(result)
            } catch (e: Exception) {
                throw SerializationException("Migration to target version ${migration.targetVersion} failed", e)
            }
        }
        return result
    }
}

data class Migration(
    val targetVersion: Version,
    val transform: (JsonElement) -> JsonElement
)
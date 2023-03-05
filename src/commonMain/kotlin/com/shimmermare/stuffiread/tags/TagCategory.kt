package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagCategoryId.Companion.None
import com.shimmermare.stuffiread.util.JsonVersionedSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import java.awt.Color

/**
 * Tag categories are used to group tags by common properties.
 */
@Serializable
data class TagCategory(
    val id: TagCategoryId = None,
    /**
     * Unique displayed name.
     */
    val name: TagCategoryName,
    /**
     * Optional displayed description.
     */
    val description: TagCategoryDescription = TagCategoryDescription.NONE,
    /**
     * Order by which tags are sorted on display.
     */
    val sortOrder: Int = 0,
    /**
     * All tags of type will be displayed using this color.
     */
    val color: Int = DEFAULT_COLOR,
    val created: Instant = Instant.fromEpochSeconds(0),
    val updated: Instant = created,
) {
    init {
        require(updated >= created) {
            "Updated date ($updated) is before created date ($created)"
        }
    }

    companion object {
        const val VERSION: UInt = 1u

        val DEFAULT_COLOR: Int = Color(0, 180, 255).rgb

        val DEFAULT_ORDER: Comparator<TagCategory> = Comparator
            .comparing(TagCategory::sortOrder)
            .thenComparing(TagCategory::name)
            .thenComparing(TagCategory::id)
    }
}

/**
 * TODO: Needs to be used explicitly for now due to bug: https://github.com/Kotlin/kotlinx.serialization/issues/1438
 * Replace with [Serializer] when fixed.
 */
object TagCategorySerializer : JsonVersionedSerializer<TagCategory>(
    currentVersion = TagCategory.VERSION,
    migrations = listOf(
        // Example:
        // Migration(1u) {
        //     JsonObject(it.jsonObject + ("newProperty" to JsonPrimitive("new value")))
        // }
    ),
    actualSerializer = TagCategory.serializer()
)
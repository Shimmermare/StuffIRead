package com.shimmermare.stuffiread.tags

import com.shimmermare.stuffiread.tags.TagId.Companion.None
import com.shimmermare.stuffiread.util.JsonVersionedSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

/**
 * Represents discrete characteristic about the story.
 */
@Serializable
data class Tag(
    val id: TagId = None,
    /**
     * Unique displayed name.
     */
    val name: TagName,
    /**
     * @see TagCategory
     */
    val categoryId: TagCategoryId,
    /**
     * Displayed optional tag description.
     */
    val description: TagDescription = TagDescription.NONE,

    /**
     * Tags that are implied to also be present when this tag is present.
     * Note: implied tags are allowed to form implication cycles. That just means that all tags in cycle are implied.
     */
    val impliedTagIds: Set<TagId> = emptySet(),
    val created: Instant = Instant.fromEpochSeconds(0),
    val updated: Instant = created,
) {
    init {
        if (impliedTagIds.contains(id)) {
            throw IllegalArgumentException("Tag can't imply itself")
        }
        if (updated < created) {
            throw IllegalArgumentException("Updated date ($updated) is before created date ($created)")
        }
    }

    companion object {
        const val VERSION: UInt = 1u
    }
}

/**
 * TODO: Needs to be used explicitly for now due to bug: https://github.com/Kotlin/kotlinx.serialization/issues/1438
 * Replace with [Serializer] when fixed.
 */
object TagSerializer : JsonVersionedSerializer<Tag>(
    currentVersion = Tag.VERSION,
    migrations = listOf(
        // Example:
        // Migration(1u) {
        //     JsonObject(it.jsonObject + ("newProperty" to JsonPrimitive("new value")))
        // }
    ),
    actualSerializer = Tag.serializer()
)

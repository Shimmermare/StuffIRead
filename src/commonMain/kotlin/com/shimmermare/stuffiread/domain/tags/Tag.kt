package com.shimmermare.stuffiread.domain.tags

typealias TagId = Int

/**
 * Represents discrete characteristic about the story.
 */
data class Tag(
    val id: TagId = 0,
    /**
     * Unique displayed name.
     */
    val name: String,
    /**
     * @see TagCategory
     */
    val categoryId: TagCategoryId,
    /**
     * Displayed optional tag description.
     */
    val description: String? = null,

    /**
     * Tags that are implied to also be present when this tag is present.
     * Note: implied tags are allowed to form implication cycles. That just means that all tags in cycle are implied.
     */
    val impliedTags: Set<TagId> = emptySet(),
) {
    init {
        if (name.length > MAX_NAME_LENGTH) {
            throw IllegalArgumentException("Name length exceeded $MAX_NAME_LENGTH (${name.length})")
        }
        if (description != null && description.length > MAX_DESCRIPTION_LENGTH) {
            throw IllegalArgumentException("Description length exceeded $MAX_DESCRIPTION_LENGTH (${description.length})")
        }
        if (impliedTags.contains(id)) {
            throw IllegalArgumentException("Tag can't imply itself")
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 2000
    }
}
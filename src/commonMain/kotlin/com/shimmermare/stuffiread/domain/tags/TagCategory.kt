package com.shimmermare.stuffiread.domain.tags

import java.awt.Color

typealias TagCategoryId = Int

/**
 * Tag categories are used to group tags by common properties.
 */
data class TagCategory(
    val id: TagCategoryId = 0,
    /**
     * Unique displayed name.
     */
    val name: String,
    /**
     * Optional displayed description.
     */
    val description: String? = null,
    /**
     * Order by which tags are sorted on display.
     */
    val sortOrder: Int = 0,
    /**
     * All tags of type will be displayed using this color.
     */
    val color: Int = DEFAULT_COLOR,
) {
    init {
        if (name.length > MAX_NAME_LENGTH) {
            throw IllegalArgumentException("Name length exceeded $MAX_NAME_LENGTH (${name.length})")
        }
        if (description != null && description.length > MAX_DESCRIPTION_LENGTH) {
            throw IllegalArgumentException("Description length exceeded $MAX_DESCRIPTION_LENGTH (${description.length})")
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 2000
        val DEFAULT_COLOR: Int = Color(0, 180, 255).rgb
    }
}
package com.shimmermare.stuffiread.tags

data class TagWithCategory(
    val tag: Tag,
    val category: TagCategory,
) {
    init {
        require(tag.categoryId == category.id) { "Category does not match tag (${tag.categoryId} vs ${category.id}" }
    }

    companion object {
        val DEFAULT_ORDER: Comparator<TagWithCategory> = Comparator
            .comparing<TagWithCategory, Int> { it.category.sortOrder }
            .thenComparing<TagCategoryName> { it.category.name }
            .thenComparing<TagCategoryId> { it.category.id }
            .thenComparing<TagName> { it.tag.name }
            .thenComparing<TagId> { it.tag.id }
    }
}

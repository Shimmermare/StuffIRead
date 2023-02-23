package com.shimmermare.stuffiread.tags

data class ExtendedTag(
    val tag: Tag,
    val category: TagCategory,
    val impliedTags: List<TagWithCategory>,
    val implyingTags: List<TagWithCategory>,
) {
    init {
        require(tag.categoryId == category.id) { "Category does not match tag (${tag.categoryId} vs ${category.id}" }
        require(tag.impliedTagIds == impliedTags.map { it.tag.id }.toSet()) {
            "Implied tags don't match: ${tag.impliedTagIds} vs ${impliedTags.map { it.tag.id }}"
        }
    }

    companion object {
        val DEFAULT_ORDER: Comparator<ExtendedTag> = Comparator
            .comparing<ExtendedTag, Int> { it.category.sortOrder }
            .thenComparing<TagCategoryName> { it.category.name }
            .thenComparing<TagCategoryId> { it.category.id }
            .thenComparing<TagName> { it.tag.name }
            .thenComparing<TagId> { it.tag.id }
    }
}

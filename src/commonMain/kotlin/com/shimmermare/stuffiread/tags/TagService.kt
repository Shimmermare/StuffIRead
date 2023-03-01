package com.shimmermare.stuffiread.tags

interface TagService {
    fun getCategoryById(categoryId: TagCategoryId): TagCategory?

    fun getCategoryByIdOrThrow(categoryId: TagCategoryId) =
        getCategoryById(categoryId) ?: error("Category $categoryId not found")

    fun getCategoryByName(name: TagCategoryName): TagCategory?

    fun getCategories(): List<TagCategory>

    fun getTagCountInCategory(categoryId: TagCategoryId): UInt

    fun createCategory(category: TagCategory): TagCategory

    fun updateCategory(category: TagCategory): TagCategory

    /**
     * Will fail if [categoryId] has any tags.
     */
    fun deleteCategoryById(categoryId: TagCategoryId)

    fun doAllTagsWithIdsExist(tagIds: Iterable<TagId>): Boolean

    fun getTagById(tagId: TagId): Tag?

    fun getAllTagIdsByExplicitTagIds(tagIds: Iterable<TagId>): Set<TagId>

    fun getExtendedTagById(tagId: TagId): ExtendedTag?

    fun getExtendedTagByIdOrThrow(tagId: TagId) = getExtendedTagById(tagId) ?: error("Tag $tagId not found")

    fun getTagWithCategoryById(tagId: TagId): TagWithCategory?

    fun getTagWithCategoryByIdOrThrow(tagId: TagId) = getTagWithCategoryById(tagId) ?: error("Tag $tagId not found")

    fun getTagsWithCategoryByTagIds(tagIds: Iterable<TagId>): List<TagWithCategory>

    fun getTagByName(name: TagName): Tag?

    fun getTagsInCategory(categoryId: TagCategoryId): List<TagWithCategory>

    fun getTagsInCategoryIncludingImplied(categoryId: TagCategoryId): List<TagWithCategory>

    fun getTagsWithCategory(): List<TagWithCategory>

    fun getTagsExtended(): List<ExtendedTag>

    fun createTag(tag: Tag): Tag

    fun updateTag(tag: Tag): Tag

    fun updateTags(tags: Iterable<Tag>): List<Tag>

    /**
     * Will fail if any other tag is implying [tagId].
     */
    fun deleteTagById(tagId: TagId)

    fun changeTagsCategory(currentCategoryId: TagCategoryId, newCategoryId: TagCategoryId)
}
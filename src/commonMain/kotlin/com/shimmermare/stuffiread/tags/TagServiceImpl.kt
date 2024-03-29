package com.shimmermare.stuffiread.tags

import io.github.aakira.napier.Napier
import kotlinx.coroutines.*

/**
 * Implementation: holds entire tree in memory and save it on any update.
 */
class TagServiceImpl(
    private val tagTreeService: TagTreeService,
) : TagService {

    private var tree: TagTree

    init {
        runBlocking(Dispatchers.IO) {
            tree = tagTreeService.getTree()
        }
    }

    override fun getCategoryById(categoryId: TagCategoryId): TagCategory? {
        return tree.getCategory(categoryId)
    }

    override fun getCategoryByName(name: TagCategoryName): TagCategory? {
        return tree.getCategoryByName(name)
    }

    override fun getTagCountInCategory(categoryId: TagCategoryId): UInt {
        return tree.getTagCountInCategory(categoryId)
    }

    override fun getCategories(): List<TagCategory> {
        return tree.categories.toList()
    }

    override fun createCategory(category: TagCategory): TagCategory {
        val (tree, created) = tree.copyAndCreateCategory(category)
        this.tree = tree
        saveTreeAsync()
        return created
    }

    override fun updateCategory(category: TagCategory): TagCategory {
        val (tree, updated) = tree.copyAndUpdateCategory(category)
        this.tree = tree
        saveTreeAsync()
        return updated
    }

    override fun deleteCategoryById(categoryId: TagCategoryId) {
        tree = tree.deleteCategory(categoryId)
        saveTreeAsync()
    }

    override fun tagExistsById(tagId: TagId): Boolean {
        return tree.tagExistsById(tagId)
    }

    override fun doAllTagsWithIdsExist(tagIds: Iterable<TagId>): Boolean {
        return tree.doAllTagsWithIdsExist(tagIds)
    }

    override fun getTagById(tagId: TagId): Tag? {
        return tree.getTag(tagId)
    }

    override fun getAllTagIdsByExplicitTagIds(tagIds: Iterable<TagId>): Set<TagId> {
        val result = mutableSetOf<TagId>()
        tagIds.forEach { tagId ->
            val extendedTag = tree.getExtendedTag(tagId)
            extendedTag?.impliedTags?.forEach { result.add(it.tag.id) }
        }
        return result
    }

    override fun getExtendedTagById(tagId: TagId): ExtendedTag? {
        return tree.getExtendedTag(tagId)
    }

    override fun getExtendedTagsByIds(tagIds: Iterable<TagId>): List<ExtendedTag> {
        return tagIds.mapNotNull { tree.getExtendedTag(it) }
    }

    override fun getTagWithCategoryById(tagId: TagId): TagWithCategory? {
        return tree.getTagWithCategory(tagId)
    }

    override fun getTagsWithCategoryByIds(tagIds: Iterable<TagId>): List<TagWithCategory> {
        return tree.getTagsWithCategoryByIds(tagIds)
    }

    override fun getTagsWithCategoryByIdsIncludingImplied(tagIds: Iterable<TagId>): List<TagWithCategory> {
        val result = hashMapOf<TagId, TagWithCategory>()
        val tree = tree
        tagIds.forEach { tagId ->
            val extended = tree.getExtendedTag(tagId) ?: return@forEach

            result[tagId] = tree.getTagWithCategory(extended.tag.id)!!
            extended.impliedTags.forEach { result.putIfAbsent(it.tag.id, it) }
            extended.indirectlyImpliedTags.forEach { result.putIfAbsent(it.tag.id, it) }
        }
        return result.values.toList()
    }

    override fun getTagByName(name: TagName): Tag? {
        return tree.getTagByName(name)
    }

    override fun getTagsInCategory(categoryId: TagCategoryId): List<TagWithCategory> {
        return tree.getTagsInCategory(categoryId)
    }

    override fun getTagsInCategoryIncludingImplied(categoryId: TagCategoryId): List<TagWithCategory> {
        return tree.getTagsInCategoryIncludingImplied(categoryId)
    }

    override fun getTags(): List<Tag> {
        return tree.tags.toList()
    }

    override fun getTagsWithCategory(): List<TagWithCategory> {
        return tree.getTagsWithCategory()
    }

    override fun getTagsExtended(): List<ExtendedTag> {
        return tree.getTagsExtended()
    }

    override fun createTag(tag: Tag): Tag {
        val (tree, created) = tree.copyAndCreateTag(tag)
        this.tree = tree
        saveTreeAsync()
        return created
    }

    override fun updateTag(tag: Tag): Tag {
        val (tree, updatedList) = tree.copyAndUpdateTags(listOf(tag))
        val updated = updatedList[0]
        this.tree = tree
        saveTreeAsync()
        return updated
    }

    override fun updateTags(tags: Iterable<Tag>): List<Tag> {
        val (tree, updated) = tree.copyAndUpdateTags(tags)
        this.tree = tree
        saveTreeAsync()
        return updated
    }

    override fun deleteTagById(tagId: TagId) {
        tree = tree.deleteTag(tagId)
        saveTreeAsync()
    }

    override fun changeTagsCategory(currentCategoryId: TagCategoryId, newCategoryId: TagCategoryId) {
        require(newCategoryId != TagCategoryId.None) {
            "Can't change category to nothing"
        }
        val updatedTags = tree.getTagsInCategory(currentCategoryId).map {
            it.tag.copy(categoryId = newCategoryId)
        }
        val (tree, _) = tree.copyAndUpdateTags(updatedTags)
        this.tree = tree
        saveTreeAsync()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveTreeAsync() {
        GlobalScope.launch {
            try {
                tagTreeService.updateTree(tree)
            } catch (e: Exception) {
                Napier.e(e) { "Failed to save tag tree" }
            }
        }
    }
}


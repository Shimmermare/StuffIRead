package com.shimmermare.stuffiread.tags

import kotlinx.datetime.Clock

/**
 * Represents immutable valid tag tree.
 * Tag categories are included in tag tree to ensure validity (e.g. tag can't be valid if category is missing).
 *
 * Tag tree is immutable because it's rarely edited.
 */
class TagTree private constructor(
    private val categoriesById: Map<TagCategoryId, TagCategory> = emptyMap(),
    private val tagsById: Map<TagId, Tag> = emptyMap(),
) {
    val categories: Collection<TagCategory> get() = categoriesById.values
    val tags: Collection<Tag> get() = tagsById.values

    private val categoriesByNameLowercase: Map<String, TagCategory> by lazy { categories.associateBy { it.name.value.lowercase() } }

    private val tagsWithCategoryByIdCache: MutableMap<TagId, TagWithCategory> = mutableMapOf()

    private val extendedTagsByIdCache: MutableMap<TagId, ExtendedTag> = mutableMapOf()

    private val impliedTagsByTagId: Map<TagId, List<TagWithCategory>> by lazy { buildImpliedMap() }
    private val indirectlyImpliedTagsByTagId: Map<TagId, List<TagWithCategory>> by lazy { buildIndirectlyImpliedMap() }

    private val implyingTagsByTagId: Map<TagId, List<TagWithCategory>> by lazy { buildImplyingMap() }
    private val indirectlyImplyingTagsByTagId: Map<TagId, List<TagWithCategory>> by lazy { buildIndirectlyImplyingMap() }

    private val tagsByNameLowercase: Map<String, Tag> by lazy { tags.associateBy { it.name.value.lowercase() } }

    private val tagsByCategoryId: Map<TagCategoryId, List<TagWithCategory>> by lazy {
        tags.groupBy({ it.categoryId }) { getTagWithCategory(it.id)!! }
    }

    init {
        categoriesById.forEach { (id, _) ->
            require(id != TagCategoryId.None) {
                "Category can't have 0 ID"
            }
        }
        tagsById.forEach { (id, tag) ->
            require(id != TagId.None) {
                "Tag can't have 0 ID"
            }
            require(categoriesById.containsKey(tag.categoryId)) {
                "Tag $id has missing category ${tag.categoryId}"
            }
            tag.impliedTagIds.forEach { impliedTagId ->
                require(tagsById.containsKey(impliedTagId)) {
                    "Tag $id is implying missing tag $impliedTagId"
                }
            }
        }
    }

    constructor(
        categories: Iterable<TagCategory> = emptyList(),
        tags: Iterable<Tag> = emptyList()
    ) : this(
        categories.associateByTo(mutableMapOf()) { it.id },
        tags.associateByTo(mutableMapOf()) { it.id }
    )

    fun getCategory(categoryId: TagCategoryId): TagCategory? = categoriesById[categoryId]

    fun getCategoryByName(name: TagCategoryName): TagCategory? = categoriesByNameLowercase[name.value.lowercase()]

    fun getTagsInCategory(categoryId: TagCategoryId): List<TagWithCategory> =
        tagsByCategoryId[categoryId] ?: emptyList()

    fun getTagsInCategoryIncludingImplied(categoryId: TagCategoryId): List<TagWithCategory> {
        val explicitTags = tagsByCategoryId[categoryId]
        if (explicitTags.isNullOrEmpty()) return emptyList()
        val result = mutableMapOf<TagId, TagWithCategory>()
        explicitTags.forEach { result[it.tag.id] = it }
        explicitTags.mapNotNull { getExtendedTag(it.tag.id) }.forEach {
            it.impliedTags.forEach { impliedTag ->
                result.putIfAbsent(impliedTag.tag.id, impliedTag)
            }
        }
        return result.values.toList()
    }

    fun getTagCountInCategory(categoryId: TagCategoryId): UInt = tagsByCategoryId[categoryId]?.size?.toUInt() ?: 0u

    fun tagExistsById(tagId: TagId): Boolean = tagsById.containsKey(tagId)

    fun doAllTagsWithIdsExist(tagIds: Iterable<TagId>): Boolean = tagIds.all { tagsById.containsKey(it) }

    fun getTag(tagId: TagId): Tag? = tagsById[tagId]

    fun getTagWithCategory(tagId: TagId): TagWithCategory? {
        var result = tagsWithCategoryByIdCache[tagId]
        if (result != null) return result

        val tag = getTag(tagId) ?: return null
        result = TagWithCategory(tag, getCategory(tag.categoryId)!!)
        tagsWithCategoryByIdCache[tagId] = result
        return result
    }

    fun getTagsWithCategoryByIds(tagIds: Iterable<TagId>): List<TagWithCategory> {
        return tagIds.mapNotNull { getTagWithCategory(it) }
    }

    fun getExtendedTag(tagId: TagId): ExtendedTag? {
        var result = extendedTagsByIdCache[tagId]
        if (result != null) return result

        val tag = getTag(tagId) ?: return null
        result = ExtendedTag(
            tag = tag,
            category = getCategory(tag.categoryId)!!,
            impliedTags = impliedTagsByTagId.getOrDefault(tagId, emptyList()),
            indirectlyImpliedTags = indirectlyImpliedTagsByTagId.getOrDefault(tagId, emptyList()),
            implyingTags = implyingTagsByTagId.getOrDefault(tagId, emptyList()),
            indirectlyImplyingTags = indirectlyImplyingTagsByTagId.getOrDefault(tagId, emptyList())
        )
        extendedTagsByIdCache[tagId] = result
        return result
    }

    fun getTagByName(name: TagName): Tag? {
        return tagsByNameLowercase[name.value.lowercase()]
    }

    fun getTagsWithCategory(): List<TagWithCategory> {
        return tags.mapNotNull { getTagWithCategory(it.id) }
    }

    fun getTagsExtended(): List<ExtendedTag> {
        return tagsById.keys.mapNotNull { getExtendedTag(it) }
    }

    fun copyAndUpdateCategory(category: TagCategory): CopyTreeResult<TagCategory> {
        require(categoriesById.containsKey(category.id)) {
            "Category ${category.id} doesn't exist"
        }
        requireUniqueCategoryName(category.name)
        val updated = category.copy(updated = Clock.System.now())
        return CopyTreeResult(
            TagTree(
                categoriesById + (updated.id to updated),
                tagsById
            ),
            updated
        )
    }

    fun copyAndCreateCategory(category: TagCategory): CopyTreeResult<TagCategory> {
        require(category.id == TagCategoryId.None) {
            "Category to create already has ID ${category.id}"
        }
        requireUniqueCategoryName(category.name)
        val createdTs = Clock.System.now()
        val created = category.copy(
            id = nextFreeCategoryId(),
            created = createdTs,
            updated = createdTs,
        )
        return CopyTreeResult(
            TagTree(
                categoriesById + (created.id to created),
                tagsById
            ),
            created
        )
    }

    private fun requireUniqueCategoryName(name: TagCategoryName) {
        val lowered = name.value.lowercase()
        require(!categoriesByNameLowercase.containsKey(lowered)) {
            "Name '$name' is already taken by ${categoriesByNameLowercase[lowered]!!.id}"
        }
    }

    fun deleteCategory(categoryId: TagCategoryId): TagTree {
        require(categoriesById.containsKey(categoryId)) {
            "Category $categoryId doesn't exist"
        }
        require(tagsByCategoryId[categoryId].isNullOrEmpty()) {
            val tagIds = tagsByCategoryId[categoryId]!!.map { it.tag.id }
            "Category $categoryId can't be deleted because it contains tags: $tagIds"
        }
        return TagTree(
            categoriesById - categoryId,
            tagsById
        )
    }

    fun copyAndUpdateTag(tag: Tag): CopyTreeResult<Tag> {
        require(tagsById.containsKey(tag.id)) {
            "Tag ${tag.id} doesn't exist"
        }
        requireUniqueTagName(tag.name)
        val updated = tag.copy(updated = Clock.System.now())
        return CopyTreeResult(
            TagTree(
                categoriesById,
                tagsById + (updated.id to updated),
            ),
            updated
        )
    }

    fun copyAndUpdateTags(tags: Iterable<Tag>): CopyTreeResult<List<Tag>> {
        val updatedTags = tags.associate { tag ->
            require(tagsById.containsKey(tag.id)) {
                "Tag ${tag.id} doesn't exist"
            }
            requireUniqueTagName(tag.name)
            tag.id to tag.copy(updated = Clock.System.now())
        }
        return CopyTreeResult(
            TagTree(
                categoriesById,
                tagsById + updatedTags,
            ),
            updatedTags.values.toList()
        )
    }

    fun copyAndCreateTag(tag: Tag): CopyTreeResult<Tag> {
        require(tag.id == TagId.None) {
            "Tag to create already has ID ${tag.id}"
        }
        requireUniqueTagName(tag.name)
        val createdTs = Clock.System.now()
        val created = tag.copy(
            id = nextFreeTagId(),
            created = createdTs,
            updated = createdTs,
        )
        return CopyTreeResult(
            TagTree(
                categoriesById,
                tagsById + (created.id to created),
            ),
            created
        )
    }

    private fun requireUniqueTagName(name: TagName) {
        val lowered = name.value.lowercase()
        require(!tagsByNameLowercase.containsKey(lowered)) {
            "Name '$name' is already taken by ${tagsByNameLowercase[lowered]!!.id}"
        }
    }

    fun deleteTag(tagId: TagId): TagTree {
        require(tagsById.containsKey(tagId)) {
            "Tag $tagId doesn't exist"
        }
        require(!implyingTagsByTagId.containsKey(tagId)) {
            "Tag $tagId can't be deleted because some tags are implying it: ${implyingTagsByTagId[tagId]!!.map { it.tag.id }}"
        }
        return TagTree(
            categoriesById,
            tagsById - tagId
        )
    }

    private fun nextFreeCategoryId(): TagCategoryId {
        return TagCategoryId((categoriesById.maxOfOrNull { it.key.value } ?: 0u) + 1u)
    }

    private fun nextFreeTagId(): TagId {
        return TagId((tagsById.maxOfOrNull { it.key.value } ?: 0u) + 1u)
    }

    private fun buildImpliedMap(): Map<TagId, List<TagWithCategory>> {
        return tags.associateBy({ it.id }) { tag ->
            tag.impliedTagIds.map { getTagWithCategory(it)!! }
        }.filterValues { it.isNotEmpty() }
    }

    private fun buildIndirectlyImpliedMap(): Map<TagId, List<TagWithCategory>> {
        return tags.associate { tag ->
            fun recursiveGetAllImplied(tagId: TagId, implied: MutableSet<TagId>) {
                val implyingTag = getTag(tagId)!!
                if (implyingTag.impliedTagIds.isEmpty()) return

                // Prevent cycles
                val toVisit = implyingTag.impliedTagIds.filter { !implied.contains(it) && tag.id != it }
                // Exclude directly implied and itself
                implied.addAll(implyingTag.impliedTagIds - tag.impliedTagIds - tag.id)
                toVisit.forEach { recursiveGetAllImplied(it, implied) }
            }

            val impliedIds = mutableSetOf<TagId>()
            recursiveGetAllImplied(tag.id, impliedIds)
            tag.id to impliedIds.map { getTagWithCategory(it)!! }
        }.filterValues { it.isNotEmpty() }
    }

    private fun buildImplyingMap(): Map<TagId, List<TagWithCategory>> {
        val directlyImplying = mutableMapOf<TagId, MutableSet<TagId>>()
        tags.forEach { tag ->
            tag.impliedTagIds.forEach { impliedId ->
                directlyImplying.computeIfAbsent(impliedId) { mutableSetOf() }.add(tag.id)
            }
        }
        return directlyImplying.mapValues { (_, value) -> value.map { getTagWithCategory(it)!! } }
    }

    private fun buildIndirectlyImplyingMap(): Map<TagId, List<TagWithCategory>> {
        val directlyImplying = mutableMapOf<TagId, MutableSet<TagId>>()
        tags.forEach { tag ->
            tag.impliedTagIds.forEach { impliedId ->
                directlyImplying.computeIfAbsent(impliedId) { mutableSetOf() }.add(tag.id)
            }
        }
        return tags.associate { tag ->
            val tagDirectlyImplying = directlyImplying[tag.id] ?: emptySet()

            fun recursiveGetAllImplying(tagId: TagId, implying: MutableSet<TagId>) {
                val direct = directlyImplying[tagId] ?: emptySet()
                if (direct.isEmpty()) return

                // Prevent cycles
                val toVisit = direct.filter { !implying.contains(it) && tag.id != it }
                // Exclude directly implying and itself
                implying.addAll(direct - tagDirectlyImplying - tag.id)
                toVisit.forEach { recursiveGetAllImplying(it, implying) }
            }

            val implyingIds = mutableSetOf<TagId>()
            recursiveGetAllImplying(tag.id, implyingIds)
            tag.id to implyingIds.map { getTagWithCategory(it)!! }
        }.filterValues { it.isNotEmpty() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagTree

        if (categoriesById != other.categoriesById) return false
        if (tagsById != other.tagsById) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoriesById.hashCode()
        result = 31 * result + tagsById.hashCode()
        return result
    }

    override fun toString(): String {
        return "TagTree(categories=${categoriesById.values}, tags=${tagsById.values})"
    }

    data class CopyTreeResult<T>(
        val tree: TagTree,
        val result: T
    )
}
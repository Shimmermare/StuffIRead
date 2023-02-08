package com.shimmermare.stuffiread.domain.tags

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.data.StoryDatabase
import com.shimmermare.stuffiread.data.tags.TagCategoryDatasource
import com.shimmermare.stuffiread.data.tags.TagDatasource

class TagService(
    storyDatabase: StoryDatabase
) {
    private val database: Database = storyDatabase.database
    private val tagDatasource: TagDatasource = storyDatabase.tagDatasource
    private val tagCategoryDatasource: TagCategoryDatasource = storyDatabase.tagCategoryDatasource

    fun getById(id: TagId): Tag? {
        return database.transactionWithResult {
            tagDatasource.findById(id)
        }
    }

    fun getExtendedByIdOrThrow(id: TagId): ExtendedTag = getExtendedById(id) ?: error("Tag $id not found")

    fun getExtendedById(id: TagId): ExtendedTag? {
        return database.transactionWithResult {
            val tag = tagDatasource.findById(id) ?: return@transactionWithResult null
            val category = tagCategoryDatasource.findById(tag.categoryId)!!

            val implyingTagIds = getImplyingIds(tag.id)
            val implyingAndImpliedTags = getByIdsWithCategory(tag.impliedTags + implyingTagIds)

            ExtendedTag(
                tag = tag,
                category = category,
                impliedTags = implyingAndImpliedTags.filter { tag.impliedTags.contains(it.tag.id) },
                implyingTags = implyingAndImpliedTags.filter { implyingTagIds.contains(it.tag.id) }
            )
        }
    }

    fun getByIds(ids: Collection<TagId>): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findByIds(ids)
        }
    }

    fun getByIdsWithCategory(ids: Collection<TagId>): List<TagWithCategory> {
        return database.transactionWithResult {
            val tags = tagDatasource.findByIds(ids)
            val categoriesById = tagCategoryDatasource.findByIds(tags.map { it.categoryId }.toSet())
                .associateBy { it.id }
            tags.map { TagWithCategory(it, categoriesById[it.categoryId]!!) }
        }
    }

    fun getNameById(id: TagId): String? {
        return database.transactionWithResult {
            tagDatasource.findNameById(id)
        }
    }

    fun getIdByName(name: String): TagId? {
        return database.transactionWithResult {
            tagDatasource.findIdByName(name)
        }
    }

    fun allExistByIds(ids: Collection<TagId>): Boolean {
        return database.transactionWithResult {
            tagDatasource.findExistingIds(ids).containsAll(ids)
        }
    }

    fun getCountInCategory(categoryId: TagCategoryId): Int {
        return database.transactionWithResult {
            tagDatasource.countInCategory(categoryId)
        }
    }

    fun getInCategory(categoryId: TagCategoryId): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findInCategory(categoryId)
        }
    }

    fun getInCategoryWithImplied(categoryId: TagCategoryId): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findInCategoryWithImplied(categoryId)
        }
    }

    fun getImplying(tagId: TagId): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findImplyingTags(tagId)
        }
    }

    fun getImplyingIds(tagId: TagId): Set<TagId> {
        return database.transactionWithResult {
            tagDatasource.findImplyingIds(tagId)
        }
    }

    fun getAll(): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findAll()
        }
    }

    fun getAllWithCategories(): List<TagWithCategory> {
        return database.transactionWithResult {
            val tags = tagDatasource.findAll()
            val categoriesById = tagCategoryDatasource.findByIds(tags.map { it.categoryId }.toSet())
                .associateBy { it.id }
            tags.map { TagWithCategory(it, categoriesById[it.categoryId]!!) }
        }
    }

    fun getAllExtended(): List<ExtendedTag> {
        return database.transactionWithResult {
            val tagsById = tagDatasource.findAll().associateBy { it.id }
            val categoriesById = tagCategoryDatasource.findByIds(tagsById.map { it.value.categoryId }.toSet())
                .associateBy { it.id }

            // Looks messy lol
            val impliedByImplying: Map<TagId, List<TagWithCategory>> = tagsById.mapValues { (_, tag) ->
                tag.impliedTags.map { id ->
                    val implied = tagsById[id]!!
                    TagWithCategory(
                        implied,
                        categoriesById[implied.categoryId]!!
                    )
                }
            }
            val implyingByImplied: Map<TagId, List<TagWithCategory>> = impliedByImplying
                .flatMap { (implyingId, implied) ->
                    implied.map {
                        val implyingTag = tagsById[implyingId]!!
                        it.tag.id to TagWithCategory(implyingTag, categoriesById[implyingTag.categoryId]!!)
                    }
                }
                .groupBy({ it.first }) { it.second }

            tagsById.map { (_, it) ->
                ExtendedTag(
                    tag = it,
                    category = categoriesById[it.categoryId]!!,
                    impliedTags = impliedByImplying.getOrDefault(it.id, emptyList()),
                    implyingTags = implyingByImplied.getOrDefault(it.id, emptyList())
                )
            }
        }
    }

    fun createOrUpdate(tag: Tag): Tag {
        return database.transactionWithResult {
            if (tag.id == 0) {
                return@transactionWithResult tagDatasource.insert(tag)
            }
            tagDatasource.update(tag)
            tag
        }
    }

    fun changeTagCategory(fromCategoryId: TagCategoryId, toCategoryId: TagCategoryId) {
        database.transaction {
            tagDatasource.changeTagCategory(fromCategoryId, toCategoryId)
        }
    }

    fun deleteById(tagId: TagId) {
        database.transaction {
            tagDatasource.delete(tagId)
        }
    }
}
package com.shimmermare.stuffiread.data.tags

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.data.tags.Tag as DbTag

class TagDatasourceImpl(
    private val db: Database
) : TagDatasource {
    private val tagQueries: TagQueries = db.tagQueries
    private val tagImpliedQueries: TagImpliedQueries = db.tagImpliedQueries

    override fun findById(id: TagId): Tag? {
        return db.transactionWithResult {
            val tag = tagQueries.selectById(id).executeAsOneOrNull() ?: return@transactionWithResult null
            val impliedTags = tagImpliedQueries.selectImpliedForTag(id).executeAsList().toSet()
            toEntity(tag, impliedTags)
        }
    }

    override fun findByIds(ids: Collection<TagId>): List<Tag> {
        return db.transactionWithResult {
            val tags = tagQueries.selectByIds(ids).executeAsList()
            selectImpliedFor(tags)
        }
    }

    override fun findNameById(id: TagId): String? {
        return tagQueries.selectNameById(id).executeAsOneOrNull()
    }

    override fun findByName(name: String): Tag? {
        return db.transactionWithResult {
            val tag = tagQueries.selectByName(name).executeAsOneOrNull() ?: return@transactionWithResult null
            val impliedTags = tagImpliedQueries.selectImpliedForTag(tag.id).executeAsList().toSet()
            toEntity(tag, impliedTags)
        }
    }

    override fun findIdByName(name: String): TagId? {
        return tagQueries.selectIdByName(name).executeAsOneOrNull()
    }

    override fun findExistingIds(ids: Collection<TagId>): Set<TagId> {
        return tagQueries.selectExistingIds(ids).executeAsList().toSet()
    }

    override fun countInCategory(categoryId: TagCategoryId): Int {
        return tagQueries.countByCategory(categoryId).executeAsOne().toInt()
    }

    override fun findInCategory(categoryId: TagCategoryId): List<Tag> {
        return db.transactionWithResult {
            val tags = tagQueries.selectByCategory(categoryId).executeAsList()
            selectImpliedFor(tags)
        }
    }

    override fun findInCategoryWithImplied(categoryId: TagCategoryId): List<Tag> {
        val rows = tagQueries.selectByCategoryIncludingImplied(categoryId).executeAsList()
        val impliedByTagId = rows.groupBy({ it.impliedBy.toInt() }) { it.id }.mapValues { (_, v) -> v.toSet() }

        return rows.map {
            Tag(
                id = it.id,
                categoryId = it.categoryId,
                name = it.name,
                description = it.description,
                impliedTags = impliedByTagId[it.id] ?: emptySet()
            )
        }
    }

    override fun findImplyingTags(tagId: TagId): List<Tag> {
        return db.transactionWithResult {
            val implyingTagIds = tagImpliedQueries.selectImplyingForTag(tagId).executeAsList()
            if (implyingTagIds.isEmpty()) return@transactionWithResult emptyList()
            val implyingTags = tagQueries.selectByIds(implyingTagIds).executeAsList()
            if (implyingTags.isEmpty()) return@transactionWithResult emptyList()
            selectImpliedFor(implyingTags)
        }
    }

    override fun findAll(): List<Tag> {
        return db.transactionWithResult {
            val tags = tagQueries.selectAll().executeAsList()
            selectImpliedFor(tags)
        }
    }

    private fun selectImpliedFor(tags: List<DbTag>): List<Tag> {
        val impliedByTagId = tagImpliedQueries.selectImpliedForTags(tags.map { it.id }).executeAsList()
            .groupBy({ it.implyingTagId }) { it.impliedTagId }.mapValues { (_, v) -> v.toSet() }

        return tags.map { toEntity(it, impliedByTagId.getOrDefault(it.id, emptySet())) }
    }

    override fun insert(tag: Tag): Tag {
        return db.transactionWithResult {
            tagQueries.insert(tag.name, tag.categoryId, tag.description)
            val dbTag = tagQueries.selectLastInserted().executeAsOne()
            val result = toEntity(dbTag, tag.impliedTags)
            updateImplied(tag)
            return@transactionWithResult result
        }
    }

    override fun update(tag: Tag) {
        db.transaction {
            tagQueries.update(tag.name, tag.categoryId, tag.description, tag.id)
            updateImplied(tag)
        }
    }

    override fun changeTagCategory(fromCategoryId: TagCategoryId, toCategoryId: TagCategoryId) {
        tagQueries.changeTagCategory(fromCategoryId = fromCategoryId, toCategoryId = toCategoryId)
    }

    private fun updateImplied(tag: Tag) {
        if (tag.impliedTags.isNotEmpty()) {
            tagImpliedQueries.deleteImpliedForTagExcept(tag.id, tag.impliedTags)
            tag.impliedTags.forEach {
                tagImpliedQueries.insertOrIgnore(tag.id, it)
            }
        } else {
            tagImpliedQueries.deleteImpliedForTag(tag.id)
        }
    }

    override fun delete(id: TagId) {
        tagQueries.delete(id)
    }

    private fun toEntity(tag: DbTag, impliedTags: Set<Int>): Tag {
        return Tag(tag.id, tag.name, tag.categoryId, tag.description, impliedTags)
    }
}
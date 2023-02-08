package com.shimmermare.stuffiread.data.tags

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryDescription
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.domain.tags.TagCategoryName
import com.shimmermare.stuffiread.data.tags.TagCategory as DbTagCategory

class TagCategoryDatasourceImpl(
    private val db: Database
) : TagCategoryDatasource {
    private val queries: TagCategoryQueries = db.tagCategoryQueries

    override fun findById(id: TagCategoryId): TagCategory? {
        return queries.selectById(id).executeAsOneOrNull()?.toEntity()
    }

    override fun findByIds(ids: Collection<TagCategoryId>): List<TagCategory> {
        return queries.selectByIds(ids).executeAsList().map { it.toEntity() }
    }

    override fun findNameById(id: TagCategoryId): String? {
        return queries.selectNameById(id).executeAsOneOrNull()
    }

    override fun findColorsByIds(ids: Collection<TagCategoryId>): Map<TagCategoryId, Int> {
        return queries.selectColorsByIds(ids, mapper = { id, color -> id to color }).executeAsList().toMap()
    }

    override fun findByName(name: String): TagCategory? {
        return queries.selectByName(name).executeAsOneOrNull()?.toEntity()
    }

    override fun findIdByName(name: String): TagCategoryId? {
        return queries.selectIdByName(name).executeAsOneOrNull()
    }

    override fun findAll(): List<TagCategory> {
        return queries.selectAll().executeAsList().map { it.toEntity() }
    }

    override fun insert(tagCategory: TagCategory): TagCategory {
        return db.transactionWithResult {
            queries.insert(
                name = tagCategory.name.value,
                description = tagCategory.description.value,
                sortOrder = tagCategory.sortOrder,
                color = tagCategory.color,
                createdTs = tagCategory.created,
                updatedTs = tagCategory.updated,
            )
            queries.selectLastInserted().executeAsOne().toEntity()
        }
    }

    override fun update(tagCategory: TagCategory) {
        queries.update(
            id = tagCategory.id,
            name = tagCategory.name.value,
            description = tagCategory.description.value,
            sortOrder = tagCategory.sortOrder,
            color = tagCategory.color,
            createdTs = tagCategory.created,
            updatedTs = tagCategory.updated
        )
    }

    override fun delete(id: TagCategoryId) {
        queries.delete(id)
    }

    private fun DbTagCategory.toEntity(): TagCategory {
        return TagCategory(
            id = id,
            name = TagCategoryName(name),
            description = TagCategoryDescription.of(description),
            sortOrder = sortOrder,
            color = color,
            created = createdTs,
            updated = updatedTs,
        )
    }
}
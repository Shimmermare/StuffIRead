package com.shimmermare.stuffiread.data.tags

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.data.tags.TagCategory as DbTagCategory

class TagCategoryDatasourceImpl(
    private val db: Database
) : TagCategoryDatasource {
    private val queries: TagCategoryQueries = db.tagCategoryQueries

    override fun findById(id: TagCategoryId): TagCategory? {
        return queries.selectById(id).executeAsOneOrNull()?.toEntity()
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
            queries.insert(tagCategory.name, tagCategory.description, tagCategory.sortOrder, tagCategory.color)
            queries.selectLastInserted().executeAsOne().toEntity()
        }
    }

    override fun update(tagCategory: TagCategory) {
        queries.update(tagCategory.name, tagCategory.description, tagCategory.sortOrder, tagCategory.color, tagCategory.id)
    }

    override fun delete(id: TagCategoryId) {
        queries.delete(id)
    }

    private fun DbTagCategory.toEntity(): TagCategory {
        return TagCategory(
            id, name, description, sortOrder, color
        )
    }
}
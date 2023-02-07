package com.shimmermare.stuffiread.domain.tags

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.data.StoryDatabase
import com.shimmermare.stuffiread.data.tags.TagCategoryDatasource

class TagCategoryService(
    storyDatabase: StoryDatabase
) {
    private val database: Database = storyDatabase.database
    private val tagCategoryDatasource: TagCategoryDatasource = storyDatabase.tagCategoryDatasource

    fun existsById(id: TagCategoryId): Boolean = getNameById(id) != null

    fun getById(id: TagCategoryId): TagCategory? {
        return database.transactionWithResult {
            tagCategoryDatasource.findById(id)
        }
    }

    fun getNameById(id: TagCategoryId): String? {
        return database.transactionWithResult {
            tagCategoryDatasource.findNameById(id)
        }
    }

    fun getColorsByIds(ids: Collection<TagCategoryId>): Map<TagCategoryId, Int> {
        return database.transactionWithResult {
            tagCategoryDatasource.findColorsByIds(ids)
        }
    }

    fun getIdByName(name: String): TagCategoryId? {
        return database.transactionWithResult {
            tagCategoryDatasource.findIdByName(name)
        }
    }

    fun getAll(): List<TagCategory> {
        return database.transactionWithResult {
            tagCategoryDatasource.findAll()
        }
    }

    fun createOrUpdate(tagCategory: TagCategory): TagCategory {
        return database.transactionWithResult {
            if (tagCategory.id == 0) {
                return@transactionWithResult tagCategoryDatasource.insert(tagCategory)
            }
            tagCategoryDatasource.update(tagCategory)
            tagCategory
        }
    }

    fun deleteById(tagCategoryId: TagCategoryId) {
        database.transaction {
            tagCategoryDatasource.delete(tagCategoryId)
        }
    }
}
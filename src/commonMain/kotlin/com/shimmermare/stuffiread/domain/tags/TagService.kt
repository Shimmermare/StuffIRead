package com.shimmermare.stuffiread.domain.tags

import com.shimmermare.stuffiread.data.Database
import com.shimmermare.stuffiread.data.StoryDatabase
import com.shimmermare.stuffiread.data.tags.TagDatasource

class TagService(
    storyDatabase: StoryDatabase
) {
    private val database: Database = storyDatabase.database
    private val tagDatasource: TagDatasource = storyDatabase.tagDatasource

    fun getById(id: TagId): Tag? {
        return database.transactionWithResult {
            tagDatasource.findById(id)
        }
    }

    fun getTags(ids: Collection<TagId>): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findByIds(ids)
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

    fun getAll(): List<Tag> {
        return database.transactionWithResult {
            tagDatasource.findAll()
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
package com.shimmermare.stuffiread.data.tags

import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.domain.tags.TagId

interface TagDatasource {
    fun findById(id: TagId): Tag?
    fun findByIds(ids: Collection<TagId>): List<Tag>
    fun findNameById(id: TagId): String?
    fun findByName(name: String): Tag?
    fun findIdByName(name: String): TagId?
    fun findExistingIds(ids: Collection<TagId>): Set<TagId>
    fun countInCategory(categoryId: TagCategoryId): Int
    fun findInCategory(categoryId: TagCategoryId): List<Tag>
    fun findInCategoryWithImplied(categoryId: TagCategoryId): List<Tag>
    fun findImplyingTags(tagId: TagId): List<Tag>
    fun findAll(): List<Tag>
    fun insert(tag: Tag): Tag
    fun update(tag: Tag)
    fun changeTagCategory(fromCategoryId: TagCategoryId, toCategoryId: TagCategoryId)
    fun delete(id: TagId)
}
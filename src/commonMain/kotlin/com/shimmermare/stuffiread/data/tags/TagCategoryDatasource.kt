package com.shimmermare.stuffiread.data.tags

import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId

interface TagCategoryDatasource {
    fun findById(id: TagCategoryId): TagCategory?
    fun findNameById(id: TagCategoryId): String?
    fun findColorsByIds(ids: Collection<TagCategoryId>): Map<TagCategoryId, Int>
    fun findByName(name: String): TagCategory?
    fun findIdByName(name: String): TagCategoryId?
    fun findAll(): List<TagCategory>
    fun insert(tagCategory: TagCategory): TagCategory
    fun update(tagCategory: TagCategory)
    fun delete(id: TagCategoryId)
}
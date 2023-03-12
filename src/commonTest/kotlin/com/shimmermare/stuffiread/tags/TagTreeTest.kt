package com.shimmermare.stuffiread.tags

import kotlin.test.Test
import kotlin.test.assertFailsWith


class TagTreeTest {
    @Test
    fun `Fails on duplicate names`() {
        val categoryId = TagCategoryId(1u)
        val tree = TagTree(
            categories = listOf(
                TagCategory(id = categoryId, name = TagCategoryName("Category"))
            ),
            tags = listOf(
                Tag(id = TagId(1u), name = TagName("Tag"), categoryId = categoryId)
            )
        )

        // Non-duplicate don't fail
        tree.copyAndCreateCategory(TagCategory(name = TagCategoryName("Category2"))).let {
            it.tree.copyAndUpdateCategory(it.result.copy(name = TagCategoryName("Category3")))
        }
        tree.copyAndCreateTag(Tag(name = TagName("Tag2"), categoryId = categoryId)).let {
            it.tree.copyAndUpdateTag(it.result.copy(name = TagName("Tag3")))
        }

        assertFailsWith(IllegalArgumentException::class) {
            tree.copyAndCreateCategory(TagCategory(name = TagCategoryName("Category")))
        }
        assertFailsWith(IllegalArgumentException::class) {
            tree.copyAndCreateCategory(TagCategory(name = TagCategoryName("Category2"))).let {
                it.tree.copyAndUpdateCategory(it.result.copy(name = TagCategoryName("Category")))
            }
        }
        assertFailsWith(IllegalArgumentException::class) {
            tree.copyAndCreateTag(Tag(name = TagName("Tag"), categoryId = categoryId))
        }
        assertFailsWith(IllegalArgumentException::class) {
            tree.copyAndCreateTag(Tag(name = TagName("Tag2"), categoryId = categoryId)).let {
                it.tree.copyAndUpdateTag(it.result.copy(name = TagName("Tag")))
            }
        }
    }
}
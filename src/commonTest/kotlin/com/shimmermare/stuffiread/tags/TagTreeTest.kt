package com.shimmermare.stuffiread.tags

import kotlin.test.Test
import kotlin.test.assertFailsWith


class TagTreeTest {
    @Test
    fun `Can't create or update tag without category`() {
        val categoryId = TagCategoryId(1u)
        val categoryId2 = TagCategoryId(2u)
        val nonExistingCategoryId = TagCategoryId(3u)
        val tree = TagTree(
            categories = listOf(
                TagCategory(id = categoryId, name = TagCategoryName("Category")),
                TagCategory(id = categoryId2, name = TagCategoryName("Category2"))
            ),
        )

        tree.copyAndCreateTag(Tag(name = TagName("Tag"), categoryId = categoryId))
        assertFailsWith(IllegalArgumentException::class) {
            tree.copyAndCreateTag(Tag(name = TagName("Tag"), categoryId = nonExistingCategoryId))
        }

        tree.copyAndCreateTag(Tag(name = TagName("Tag"), categoryId = categoryId)).let {
            it.tree.copyAndUpdateTags(listOf(it.result.copy(categoryId = categoryId2)))
        }
        assertFailsWith(IllegalArgumentException::class) {
            tree.copyAndCreateTag(Tag(name = TagName("Tag"), categoryId = categoryId)).let {
                it.tree.copyAndUpdateTags(listOf(it.result.copy(categoryId = nonExistingCategoryId)))
            }
        }
    }

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
            it.tree.copyAndUpdateTags(listOf(it.result.copy(name = TagName("Tag3"))))
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
                it.tree.copyAndUpdateTags(listOf(it.result.copy(name = TagName("Tag"))))
            }
        }
    }
}
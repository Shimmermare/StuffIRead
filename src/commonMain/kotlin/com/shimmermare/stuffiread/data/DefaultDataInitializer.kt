package com.shimmermare.stuffiread.data

import com.shimmermare.stuffiread.data.tags.TagCategoryDatasource
import com.shimmermare.stuffiread.data.tags.TagDatasource
import com.shimmermare.stuffiread.domain.tags.*
import java.awt.Color
import java.time.OffsetDateTime

class DefaultDataInitializer(
    private val database: Database,
    private val tagCategoryDatasource: TagCategoryDatasource,
    private val tagDatasource: TagDatasource,
) {
    fun initDefaultData() = database.transaction {
        val series = tagCat("Series", "Series this story is based upon.", 0, Color(113, 57, 132))
        val genre = tagCat("Genre", "Story genre.", 1, Color(55, 102, 150))
        val character = tagCat("Character", "Character that plays a major role in the story.", 2, Color(25, 130, 81))

        for (i in 0..100) {
            tagCat(Math.random().toString(), Math.random().toString(), 5, Color(113, 57, 132))
        }
        tag("Example Series", series.id, "Series tag")
        tag("Example Genre", genre.id, "Genre tag")
        tag("Example Character", character.id, "Character tag")
    }

    private fun tagCat(name: String, desc: String?, sortOrder: Int, color: Color): TagCategory {
        return tagCategoryDatasource.insert(
            TagCategory(
                name = TagCategoryName(name),
                description = TagCategoryDescription.of(desc),
                sortOrder = sortOrder,
                color = color.rgb,
                created = OffsetDateTime.now()
            )
        )
    }

    private fun tag(name: String, cat: TagCategoryId, desc: String?): Tag {
        return tagDatasource.insert(
            Tag(
                name = TagName(name),
                categoryId = cat,
                description = TagDescription.of(desc),
                created = OffsetDateTime.now()
            )
        )
    }
}
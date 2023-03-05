package com.shimmermare.stuffiread.ui.pages.tag.edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.EDIT
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.Page

class EditTagPage(
    private val mode: EditTagPageMode,
    private val editingTagId: TagId = TagId.None,
    private val prefillWith: Tag,
) : Page {
    @Composable
    override fun Title(app: AppState) {
        val title = remember(mode, editingTagId) {
            when (mode) {
                CREATE -> "New tag"
                EDIT -> {
                    val tag = app.storyArchive!!.tagService.getTagByIdOrThrow(editingTagId)
                    "Tag (Editing) - ${tag.name} [${editingTagId}]"
                }
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body(app: AppState) {
        AnimatedFadeIn {
            TagForm(
                mode = mode,
                tag = prefillWith,
                onBack = {
                    when (mode) {
                        CREATE -> app.router.goTo(TagsPage())
                        EDIT -> app.router.goTo(TagInfoPage(editingTagId))
                    }
                },
                onSubmit = {
                    val tag = when (mode) {
                        CREATE -> app.storyArchive!!.tagService.createTag(it)
                        EDIT -> app.storyArchive!!.tagService.updateTag(it)
                    }
                    app.router.goTo(TagInfoPage(tag.id))
                }
            )
        }
    }

    companion object {
        fun create() = EditTagPage(
            mode = CREATE,
            prefillWith = Tag(
                name = TagName("New tag"),
                categoryId = TagCategoryId.None,
            )
        )

        fun createCopy(original: Tag) = EditTagPage(
            mode = CREATE,
            prefillWith = original.copy(
                id = TagId.None,
                name = TagName("Copy of " + original.name)
            )
        )

        fun edit(tag: Tag): EditTagPage {
            if (tag.id == TagId.None) throw IllegalArgumentException("Can edit only existing tag")
            return EditTagPage(
                mode = EDIT,
                editingTagId = tag.id,
                prefillWith = tag
            )
        }
    }
}

enum class EditTagPageMode {
    CREATE, EDIT
}
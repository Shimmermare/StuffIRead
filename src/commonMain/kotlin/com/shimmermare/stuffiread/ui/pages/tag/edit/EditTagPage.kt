package com.shimmermare.stuffiread.ui.pages.tag.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
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
    override fun Title() {
        val title = remember(mode, editingTagId) {
            when (mode) {
                CREATE -> "New tag"
                EDIT -> {
                    val tag = tagService.getTagByIdOrThrow(editingTagId)
                    "Tag (Editing) - ${tag.name} [${editingTagId}]"
                }
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body() {
        AnimatedFadeIn {
            VerticalScrollColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            ) {
                TagForm(
                    mode = mode,
                    tag = prefillWith,
                    onBack = {
                        when (mode) {
                            CREATE -> Router.goTo(TagsPage())
                            EDIT -> Router.goTo(TagInfoPage(editingTagId))
                        }
                    },
                    onSubmit = {
                        val tag = when (mode) {
                            CREATE -> tagService.createTag(it)
                            EDIT -> tagService.updateTag(it)
                        }
                        Router.goTo(TagInfoPage(tag.id))
                    }
                )
            }
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
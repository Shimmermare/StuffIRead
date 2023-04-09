package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.Tag
import com.shimmermare.stuffiread.tags.TagCategoryId
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.tags.TagName
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder.tagService
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.components.layout.VerticalScrollColumn
import com.shimmermare.stuffiread.ui.components.tag.TagForm
import com.shimmermare.stuffiread.ui.routing.Page

class EditTagPage private constructor(
    private val creationMode: Boolean,
    private val editingTagId: TagId = TagId.None,
    private val prefillWith: Tag,
) : Page {
    @Composable
    override fun Title() {
        val title = if (creationMode) {
            Strings.page_tagEdit_title_create()
        } else {
            val tag = tagService.getTagByIdOrThrow(editingTagId)
            Strings.page_tagEdit_title_edit(tag.name) + " [" + editingTagId + "]"
        }
        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body() {
        AnimatedFadeIn {
            VerticalScrollColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            ) {
                TagForm(
                    creationMode = creationMode,
                    tag = prefillWith,
                    onBack = { Router.goTo(if (creationMode) TagsPage() else TagInfoPage(editingTagId)) },
                    onSubmit = {
                        val tag = if (creationMode) tagService.createTag(it) else tagService.updateTag(it)
                        Router.goTo(TagInfoPage(tag.id))
                    }
                )
            }
        }
    }

    companion object {
        fun create() = EditTagPage(
            creationMode = true,
            prefillWith = Tag(
                name = TagName(Strings.components_tagForm_name_default_new()),
                categoryId = TagCategoryId.None,
            )
        )

        fun createCopy(original: Tag) = EditTagPage(
            creationMode = true,
            prefillWith = original.copy(
                id = TagId.None,
                name = TagName(Strings.components_tagForm_name_default_copy(original.name))
            )
        )

        fun edit(tag: Tag): EditTagPage {
            if (tag.id == TagId.None) throw IllegalArgumentException("Can edit only existing tag")
            return EditTagPage(
                creationMode = false,
                editingTagId = tag.id,
                prefillWith = tag
            )
        }
    }
}
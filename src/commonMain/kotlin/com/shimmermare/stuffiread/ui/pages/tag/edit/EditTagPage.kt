package com.shimmermare.stuffiread.ui.pages.tag.edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.domain.tags.TagName
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.animation.AnimatedFadeIn
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.CREATE
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageMode.EDIT
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPageData
import com.shimmermare.stuffiread.ui.pages.tags.TagsPage
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.PageData
import com.shimmermare.stuffiread.ui.routing.Router
import java.time.OffsetDateTime

object EditTagPage : Page<EditTagPageData> {
    override val name = "Tag"

    @Composable
    override fun Title(app: AppState, data: EditTagPageData) {
        val title = remember(data.mode, data.editingTagId) {
            when (data.mode) {
                CREATE -> "New tag"
                EDIT -> {
                    val tag = app.tagService.getById(data.editingTagId!!)!!
                    "Tag (Editing) - ${tag.name} [${data.editingTagId}]"
                }
            }
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun Body(router: Router, app: AppState, data: EditTagPageData) {
        AnimatedFadeIn {
            TagForm(
                tagCategoryService = app.tagCategoryService,
                tagService = app.tagService,
                mode = data.mode,
                tag = data.prefillWith,
                onCancel = {
                    when (data.mode) {
                        CREATE -> router.goTo(TagsPage, EmptyData)
                        EDIT -> router.goTo(TagInfoPage, TagInfoPageData(data.editingTagId!!))
                    }
                },
                onSubmit = {
                    val tag = app.tagService.createOrUpdate(it)
                    router.goTo(TagInfoPage, TagInfoPageData(tag.id))
                }
            )
        }
    }
}


data class EditTagPageData(
    val mode: EditTagPageMode,
    val editingTagId: TagId? = null,
    val prefillWith: Tag,
) : PageData {
    companion object {
        val Create = EditTagPageData(
            mode = CREATE,
            prefillWith = Tag(
                name = TagName("New tag"),
                categoryId = 0,
                created = OffsetDateTime.MIN
            )
        )

        fun createCopy(original: Tag) = EditTagPageData(
            mode = CREATE,
            prefillWith = original.copy(
                id = 0,
                name = TagName("Copy of " + original.name)
            )
        )

        fun edit(tag: Tag): EditTagPageData {
            if (tag.id == 0) throw IllegalArgumentException("Can edit only existing tag")
            return EditTagPageData(
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
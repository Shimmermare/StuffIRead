package com.shimmermare.stuffiread.ui.pages.tag.edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.ui.AppState
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
    override fun renderTopBarTitle(app: AppState, data: EditTagPageData) {
        val title by remember(data.mode, data.tag.id, data.tag.name) {
            mutableStateOf(
                when (data.mode) {
                    CREATE -> "New tag"
                    EDIT -> "Tag (Editing) - ${data.tag.name} [${data.tag.id}]"
                }
            )
        }
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    @Composable
    override fun renderBody(router: Router, app: AppState, data: EditTagPageData) {
        TagForm(
            tagCategoryService = app.tagCategoryService,
            tagService = app.tagService,
            mode = data.mode,
            tag = data.tag,
            onCancel = {
                when (data.mode) {
                    CREATE -> router.goTo(TagsPage, EmptyData)
                    EDIT -> router.goTo(TagInfoPage, TagInfoPageData(data.tag))
                }
            },
            onSubmit = {
                val tag = app.tagService.createOrUpdate(it)
                router.goTo(TagInfoPage, TagInfoPageData(tag))
            }
        )
    }
}


data class EditTagPageData(
    val mode: EditTagPageMode,
    val tag: Tag,
) : PageData {
    constructor(tag: Tag) : this(
        mode = EDIT,
        tag = tag
    ) {
        if (tag.id == 0) throw IllegalArgumentException("Can edit only existing tag")
    }

    companion object {
        val Create = EditTagPageData(
            mode = CREATE,
            tag = Tag(
                name = "New tag",
                categoryId = 0,
                created = OffsetDateTime.MIN
            )
        )

        fun createCopy(original: Tag) = EditTagPageData(
            mode = CREATE,
            tag = original.copy(
                id = 0,
                name = "Copy of " + original.name
            )
        )
    }
}

enum class EditTagPageMode {
    CREATE, EDIT
}
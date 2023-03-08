package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.pages.MutableTablePage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.routing.Router
import io.github.aakira.napier.Napier

/**
 * Page with listing of all tags with search available.
 */
class TagsPage : MutableTablePage<TagId, ExtendedTag>() {
    override fun ExtendedTag.id(): TagId = tag.id

    override fun ExtendedTag.name(): String = tag.name.value

    override suspend fun load(): Map<TagId, ExtendedTag> {
        return StoryArchiveHolder.tagService.getTagsExtended().associateBy { it.tag.id }
    }

    @Composable
    override fun LoadingError() {
        Napier.e(error) { "Failed to load tags" }

        Router.goTo(
            ErrorPage(
                title = "Failed to load tags",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    Router.goTo(TagsPage())
                })
            )
        )
    }

    override fun getUnitName(count: Int): String {
        return if (count == 1) "tag" else "tags"
    }

    override fun Router.goToCreatePage() {
        goTo(EditTagPage.create())
    }

    @Composable
    override fun DeleteDialog(item: ExtendedTag, onDeleted: () -> Unit, onDismissRequest: () -> Unit) {
        DeleteTagDialog(
            tag = item,
            onDeleted = {
                onDeleted()
                // Reload whole table because other tags can be changed too
                reload()
            },
            onDismissRequest = onDismissRequest
        )
    }

    @Composable
    override fun TableContent(
        items: Collection<ExtendedTag>,
        onDeleteRequest: (ExtendedTag) -> Unit
    ) {
        TagTable(
            tags = items,
            onRowClick = { Router.goTo(TagInfoPage(it.tag.id)) },
            onEditRequest = { Router.goTo(EditTagPage.edit(it.tag)) },
            onDeleteRequest = onDeleteRequest
        )
    }
}

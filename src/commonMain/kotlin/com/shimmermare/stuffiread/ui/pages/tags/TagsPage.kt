package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.AppState
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

    override suspend fun load(app: AppState): Map<TagId, ExtendedTag> {
        return app.storyArchive!!.tagService.getTagsExtended().associateBy { it.tag.id }
    }

    @Composable
    override fun LoadingError(app: AppState) {
        Napier.e(error) { "Failed to load tags" }

        app.router.goTo(
            ErrorPage(
                title = "Failed to load tags",
                exception = error,
                actions = listOf(ErrorPage.Action("Try Again") {
                    app.router.goTo(TagsPage())
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
    override fun DeleteDialog(app: AppState, item: ExtendedTag, onDeleted: () -> Unit, onDismiss: () -> Unit) {
        DeleteTagDialog(
            tagService = app.storyArchive!!.tagService,
            tag = item,
            onDeleted = {
                onDeleted()
                // Reload whole table because other tags can be changed too
                reload()
            },
            onDismiss = onDismiss
        )
    }

    @Composable
    override fun TableContent(
        app: AppState,
        items: Collection<ExtendedTag>,
        onDeleteRequest: (ExtendedTag) -> Unit
    ) {
        TagTable(
            router = app.router,
            tags = items,
            onRowClick = { app.router.goTo(TagInfoPage(it.tag.id)) },
            onEditRequest = { app.router.goTo(EditTagPage.edit(it.tag)) },
            onDeleteRequest = onDeleteRequest
        )
    }
}

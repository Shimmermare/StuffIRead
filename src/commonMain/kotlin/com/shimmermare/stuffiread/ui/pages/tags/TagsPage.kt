package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.domain.tags.ExtendedTag
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.pages.MutableTablePage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageData
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPageData
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Router
import io.github.aakira.napier.Napier

/**
 * Page with listing of all tags with search available.
 */
object TagsPage : MutableTablePage<EmptyData, TagId, ExtendedTag>() {
    override val name = "Tags"

    override fun ExtendedTag.id(): TagId = tag.id

    override fun ExtendedTag.name(): String = tag.name.value

    override suspend fun load(app: AppState, data: EmptyData): Map<TagId, ExtendedTag> {
        return app.tagService.getAllExtended().associateBy { it.tag.id }
    }

    @Composable
    override fun LoadingError(data: EmptyData, e: Exception?) {
        Napier.e(e) { "Failed to load tags" }
        Text("Failed to load tags", style = MaterialTheme.typography.h5)
    }

    override fun getUnitName(count: Int): String {
        return if (count == 1) "tag" else "tags"
    }

    override fun Router.goToCreatePage() {
        goTo(EditTagPage, EditTagPageData.Create)
    }

    @Composable
    override fun DeleteDialog(app: AppState, item: ExtendedTag, onDeleted: () -> Unit, onDismiss: () -> Unit) {
        DeleteTagDialog(
            tag = item,
            onConfirm = {
                app.tagService.deleteById(item.tag.id)
                onDeleted()
            },
            onDismiss = onDismiss
        )
    }

    @Composable
    override fun TableContent(
        router: Router,
        app: AppState,
        items: Collection<ExtendedTag>,
        onDeleteRequest: (ExtendedTag) -> Unit
    ) {
        TagTable(
            tags = items,
            onRowClick = { router.goTo(TagInfoPage, TagInfoPageData(it.tag.id)) },
            onEditRequest = { router.goTo(EditTagPage, EditTagPageData.edit(it.tag)) },
            onDeleteRequest = onDeleteRequest
        )
    }
}

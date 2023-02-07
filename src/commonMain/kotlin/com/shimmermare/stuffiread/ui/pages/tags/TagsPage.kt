package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.domain.tags.Tag
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.table.TableWithSearch
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPage
import com.shimmermare.stuffiread.ui.pages.tag.edit.EditTagPageData
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPage
import com.shimmermare.stuffiread.ui.pages.tag.info.TagInfoPageData
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.Router

/**
 * Page with listing of all tags with search available.
 */
object TagsPage : Page<EmptyData> {
    override val name = "Tags"

    @Composable
    override fun renderBody(router: Router, app: AppState, data: EmptyData) {
        val categories: List<TagCategory> = remember { app.tagCategoryService.getAll().toMutableStateList() }
        // SnapshotStateMap is either bugged or PITA to use, immutability FTW
        var tagsById: Map<TagId, Tag> by remember { mutableStateOf(app.tagService.getAll().associateBy { it.id }) }

        var showDeleteDialogFor: Tag? by remember { mutableStateOf(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { router.goTo(EditTagPage, EditTagPageData.Create) }
                ) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        ) {
            TableWithSearch(
                // Using copy list because for some reason SnapshotStateMap.values is not working
                remember(tagsById) { tagsById.values.toList() },
                nameGetter = { it.name },
                unitNameProvider = { if (it == 1) "tag" else "tags" }
            ) { filtered ->
                TagTable(
                    tags = filtered,
                    tagCategories = categories,
                    onClick = { router.goTo(TagInfoPage, TagInfoPageData(it)) },
                    onEdit = { router.goTo(EditTagPage, EditTagPageData(it)) },
                    onDelete = { showDeleteDialogFor = it }
                )
            }
        }

        if (showDeleteDialogFor != null) {
            DeleteTagDialog(app.tagCategoryService, app.tagService, tag = showDeleteDialogFor!!) { deleted ->
                if (deleted && showDeleteDialogFor != null) {
                    tagsById = tagsById - showDeleteDialogFor!!.id
                }
                showDeleteDialogFor = null
            }
        }
    }
}

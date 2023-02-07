package com.shimmermare.stuffiread.ui.pages.tagcategories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shimmermare.stuffiread.domain.tags.TagCategory
import com.shimmermare.stuffiread.domain.tags.TagCategoryId
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.table.TableWithSearch
import com.shimmermare.stuffiread.ui.components.tagcategory.DeleteTagCategoryDialog
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.edit.EditTagCategoryPageData
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPage
import com.shimmermare.stuffiread.ui.pages.tagcategory.info.TagCategoryInfoPageData
import com.shimmermare.stuffiread.ui.routing.EmptyData
import com.shimmermare.stuffiread.ui.routing.Page
import com.shimmermare.stuffiread.ui.routing.Router

/**
 * Page with listing of all tag categories with search available.
 */
object TagCategoriesPage : Page<EmptyData> {
    override val name = "Tag categories"

    @Composable
    override fun renderBody(router: Router, app: AppState, data: EmptyData) {
        // SnapshotStateMap is either bugged or PITA to use, immutability FTW
        var categoriesById: Map<TagCategoryId, TagCategory> by remember {
            mutableStateOf(app.tagCategoryService.getAll().associateBy { it.id })
        }

        var showDeleteDialogFor: TagCategory? by remember { mutableStateOf(null) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = { router.goTo(EditTagCategoryPage, EditTagCategoryPageData.Create) }) {
                    Icon(Icons.Filled.Add, null)
                }
            }
        ) {
            TableWithSearch(
                // Using copy list because for some reason SnapshotStateMap.values is not working
                categoriesById.values,
                nameGetter = { it.name },
                unitNameProvider = { if (it == 1) "tag category" else "tag categories" },
            ) { filtered ->
                TagCategoryTable(
                    categories = filtered,
                    onClick = { router.goTo(TagCategoryInfoPage, TagCategoryInfoPageData(it)) },
                    onEdit = { router.goTo(EditTagCategoryPage, EditTagCategoryPageData(it)) },
                    onDelete = { showDeleteDialogFor = it }
                )
            }
        }

        if (showDeleteDialogFor != null) {
            DeleteTagCategoryDialog(
                app.tagCategoryService,
                app.tagService,

                category = showDeleteDialogFor!!
            ) { deleted ->
                if (deleted && showDeleteDialogFor != null) {
                    categoriesById = categoriesById - showDeleteDialogFor!!.id
                }
                showDeleteDialogFor = null
            }
        }
    }
}

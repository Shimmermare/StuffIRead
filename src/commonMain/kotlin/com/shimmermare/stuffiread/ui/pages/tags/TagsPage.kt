package com.shimmermare.stuffiread.ui.pages.tags

import androidx.compose.runtime.Composable
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.tags.ExtendedTag
import com.shimmermare.stuffiread.tags.TagId
import com.shimmermare.stuffiread.ui.Router
import com.shimmermare.stuffiread.ui.StoryArchiveHolder
import com.shimmermare.stuffiread.ui.components.tag.DeleteTagDialog
import com.shimmermare.stuffiread.ui.pages.MutableTablePage
import com.shimmermare.stuffiread.ui.pages.error.ErrorPage
import com.shimmermare.stuffiread.ui.routing.Router
import com.shimmermare.stuffiread.ui.util.remember
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
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
                title = Strings.page_tags_failedToLoad_title.remember(),
                exception = error,
                actions = listOf(ErrorPage.Action(Strings.page_tags_failedToLoad_tryAgainButton.remember()) {
                    Router.goTo(TagsPage())
                })
            )
        )
    }

    override fun getSearchResultText() = tags_search_result

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

    companion object {
        private val tags_search_result = PluralLocalizedString(
            Strings.page_tags_search_result_zero,
            Strings.page_tags_search_result_one,
            Strings.page_tags_search_result_two,
            Strings.page_tags_search_result_few,
            Strings.page_tags_search_result_many,
            Strings.page_tags_search_result_other,
        )
    }
}

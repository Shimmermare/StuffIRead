package com.shimmermare.stuffiread.ui.pages.error

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.AppState
import com.shimmermare.stuffiread.ui.components.error.ErrorCard
import com.shimmermare.stuffiread.ui.routing.Page

class ErrorPage(
    private val title: String,
    private val description: String? = null,
    private val exception: Exception? = null,
    private val suggestion: String? = null,
    private val actions: List<Action> = emptyList()
) : Page {
    @Composable
    override fun Body(app: AppState) {
        Scaffold(
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                ) {
                    actions.forEach {
                        Button(onClick = it.action) {
                            Text(it.text, style = MaterialTheme.typography.h6)
                        }
                    }
                }
            }
        ) {
            Box {
                val lazyColumnState = rememberLazyListState()
                LazyColumn(
                    state = lazyColumnState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        ErrorCard(
                            title = title,
                            description = description,
                            exception = exception,
                            suggestion = suggestion
                        )
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(lazyColumnState)
                )
            }
        }
    }

    data class Action(
        val text: String,
        val action: () -> Unit,
    )
}


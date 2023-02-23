package com.shimmermare.stuffiread.ui.pages.error

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shimmermare.stuffiread.ui.AppState
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
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        SelectionContainer {
                            Text(title, style = MaterialTheme.typography.h5)
                        }

                    }
                    if (description != null) {
                        item {
                            SelectionContainer {
                                Text(description)
                            }
                        }
                    }
                    if (exception != null) {
                        item {
                            Column {
                                Text("Exception stack trace:", style = MaterialTheme.typography.h6)
                                SelectionContainer {
                                    // Default font can't display tab char
                                    val text = exception.stackTraceToString().replace("\t", "    ")
                                    Text(text)
                                }
                            }
                        }
                    }
                    if (suggestion != null) {
                        item {
                            SelectionContainer {
                                Text(suggestion)
                            }
                        }
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


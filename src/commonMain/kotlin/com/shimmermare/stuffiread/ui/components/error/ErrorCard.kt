package com.shimmermare.stuffiread.ui.components.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCard(
    info: ErrorInfo,
    modifier: Modifier = Modifier.fillMaxSize().padding(20.dp),
) {
    ErrorCard(
        title = info.title,
        description = info.description,
        exception = info.exception,
        suggestion = info.suggestion,
        modifier = modifier
    )
}

@Composable
fun ErrorCard(
    title: String,
    description: String? = null,
    exception: Exception? = null,
    suggestion: String? = null,
    modifier: Modifier = Modifier.fillMaxSize().padding(20.dp),
) {
    SelectionContainer {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(title, style = MaterialTheme.typography.h5)

            if (description != null) {
                Text(description)
            }

            if (exception != null) {
                Column {
                    Text("Exception stack trace:", style = MaterialTheme.typography.h6)
                    // Default font can't display tab char
                    val text = exception.stackTraceToString().replace("\t", "    ")
                    Text(text)
                }
            }

            if (suggestion != null) {
                Text(suggestion)

            }
        }
    }
}

data class ErrorInfo(
    val title: String,
    val description: String? = null,
    val exception: Exception? = null,
    val suggestion: String? = null,
)
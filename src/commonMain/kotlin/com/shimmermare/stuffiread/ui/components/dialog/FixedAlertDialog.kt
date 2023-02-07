package com.shimmermare.stuffiread.ui.components.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Alert dialog with added border.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FixedAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    dialogProvider: AlertDialogProvider = PopupAlertDialogProvider
) {
    val customModifier = Modifier
        .defaultMinSize(minWidth = 350.dp)
        .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(1))
        .then(modifier)
    AlertDialog(
        onDismissRequest,
        confirmButton,
        customModifier,
        dismissButton,
        title,
        text,
        shape,
        backgroundColor,
        contentColor,
        dialogProvider
    )
}
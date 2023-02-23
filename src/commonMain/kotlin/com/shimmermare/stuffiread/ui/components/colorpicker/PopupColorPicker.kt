package com.shimmermare.stuffiread.ui.components.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.components.text.FixedOutlinedTextField
import com.shimmermare.stuffiread.ui.util.ColorUtils.blueInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.greenInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.parseHexColor
import com.shimmermare.stuffiread.ui.util.ColorUtils.redInt
import com.shimmermare.stuffiread.ui.util.ColorUtils.toHexColor
import com.shimmermare.stuffiread.ui.util.ColorUtils.with

/**
 * Color picker with customizable button and option to enter colors using
 */
@Composable
fun PopupColorPicker(
    color: Color,
    button: @Composable (Color) -> Unit = {
        Box(
            modifier = Modifier.background(color, CircleShape).size(48.dp)
        )
    },
    pickAlpha: Boolean = false,
    onPick: (Color) -> Unit
) {
    var currentColor by remember(color) { mutableStateOf(color) }
    var show by remember { mutableStateOf(false) }

    // Wrap in row and additional box to align top-end of button but outside
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(
            modifier = Modifier.clickable { show = true }
        ) {
            button.invoke(currentColor)
        }
        if (show) {
            Box {
                Popup(
                    alignment = Alignment.TopStart,
                    focusable = true,
                    onDismissRequest = {
                        show = false
                        onPick.invoke(currentColor)
                    }
                ) {
                    PopupContent {
                        Column(
                            modifier = Modifier.width(200.dp).padding(5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            ClassicColorPicker(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1F),
                                color = HsvColor.from(currentColor),
                                showAlphaBar = pickAlpha,
                                onColorChanged = { currentColor = it.toColor() }
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("HEX")
                                HexColorInput(
                                    currentColor,
                                    modifier = Modifier.height(36.dp).fillMaxWidth(),
                                    onColorChange = { currentColor = it }
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("R")
                                RGBColorInput(
                                    color = currentColor.redInt,
                                    modifier = Modifier.height(36.dp).weight(1F),
                                    onColorChange = { currentColor = currentColor.with(red = it) }
                                )
                                Text("G")
                                RGBColorInput(
                                    color = currentColor.greenInt,
                                    modifier = Modifier.height(36.dp).weight(1F),
                                    onColorChange = { currentColor = currentColor.with(green = it) }
                                )
                                Text("B")
                                RGBColorInput(
                                    color = currentColor.blueInt,
                                    modifier = Modifier.height(36.dp).weight(1F),
                                    onColorChange = { currentColor = currentColor.with(blue = it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HexColorInput(color: Color, modifier: Modifier = Modifier, onColorChange: (Color) -> Unit) {
    var focused by remember { mutableStateOf(false) }
    var overrideText by remember(focused) { mutableStateOf(color.toHexColor().uppercase()) }
    var error by remember(focused, color) { mutableStateOf(false) }

    FixedOutlinedTextField(
        value = if (focused) overrideText else color.toHexColor().uppercase(),
        modifier = modifier.onFocusChanged { focused = it.isFocused },
        isError = error,
        onValueChange = {
            overrideText = it
            try {
                val result = it.parseHexColor()
                error = false
                onColorChange.invoke(result)
            } catch (e: Exception) {
                error = true
            }
        }
    )
}

@Composable
private fun RGBColorInput(color: Int, modifier: Modifier = Modifier, onColorChange: (Int) -> Unit) {
    var focused by remember { mutableStateOf(false) }
    var overrideText by remember(focused) { mutableStateOf(color.toString()) }
    var error by remember(focused, color) { mutableStateOf(false) }

    FixedOutlinedTextField(
        value = if (focused) overrideText else color.toString(),
        modifier = modifier.onFocusChanged { focused = it.isFocused },
        isError = error,
        onValueChange = {
            overrideText = it
            try {
                val result = it.toInt()
                if (result !in 0..255) throw IllegalArgumentException("Color value out of bounds")
                error = false
                onColorChange.invoke(result)
            } catch (e: Exception) {
                error = true
            }
        }
    )
}
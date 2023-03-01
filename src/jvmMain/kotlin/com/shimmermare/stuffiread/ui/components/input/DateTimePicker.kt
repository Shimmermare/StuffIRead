package com.shimmermare.stuffiread.ui.components.input

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.github.lgooddatepicker.components.DatePickerSettings
import com.github.lgooddatepicker.components.DateTimePicker
import com.github.lgooddatepicker.components.TimePickerSettings
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import javax.swing.BoxLayout
import javax.swing.JPanel
import java.time.LocalDateTime as JavaLocalDateTime

@Composable
actual fun DateTimePicker(
    value: LocalDateTime,
    onValueChange: (LocalDateTime) -> Unit,
    modifier: Modifier
) {
    val picker = remember {
        Picker(value.toJavaLocalDateTime()).apply {
            dateTimePicker.addDateTimeChangeListener {
                onValueChange(it.newDateTimeStrict.toKotlinLocalDateTime())
            }
        }
    }
    SwingPanel(
        background = MaterialTheme.colors.background,
        factory = { picker },
        modifier = modifier,
        update = { it.dateTime = value.toJavaLocalDateTime() }
    )
}

private class Picker(
    var dateTime: JavaLocalDateTime
) : JPanel() {
    val dateTimePicker = DateTimePicker(DatePickerSettings(), TimePickerSettings()).apply { dateTimeStrict = dateTime }

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(dateTimePicker)
    }
}
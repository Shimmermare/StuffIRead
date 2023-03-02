package com.shimmermare.stuffiread.ui.components.input

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.toArgb
import com.github.lgooddatepicker.components.DatePickerSettings
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea
import com.github.lgooddatepicker.components.DateTimePicker
import com.github.lgooddatepicker.components.TimePicker
import com.github.lgooddatepicker.components.TimePickerSettings
import com.github.lgooddatepicker.components.TimePickerSettings.TimeArea
import com.github.lgooddatepicker.zinternaltools.TimeMenuPanel
import com.shimmermare.stuffiread.ui.theme.LocalTheme
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.reflect.Field
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import java.time.LocalDateTime as JavaLocalDateTime


@Composable
actual fun DateTimePicker(
    value: LocalDateTime,
    onValueChange: (LocalDateTime) -> Unit,
    modifier: Modifier
) {
    val colors = MaterialTheme.colors
    val picker = remember(LocalTheme.current) {
        Picker(
            dateTime = value.toJavaLocalDateTime(),
            primaryColor = Color(colors.primary.toArgb()),
            backgroundColor = Color(colors.background.toArgb()),
            surfaceColor = Color(colors.surface.toArgb()),
            errorColor = Color(colors.error.toArgb()),
            onPrimaryColor = Color(colors.onPrimary.toArgb()),
            onSurfaceColor = Color(colors.onSurface.toArgb()),
        ).apply {
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
    var dateTime: JavaLocalDateTime,
    private val primaryColor: Color,
    private val backgroundColor: Color,
    private val surfaceColor: Color,
    private val errorColor: Color,
    private val onPrimaryColor: Color,
    private val onSurfaceColor: Color,
) : JPanel() {
    val dateTimePicker: DateTimePicker

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        dateTimePicker = DateTimePicker(
            DatePickerSettings().apply {
                setColor(DateArea.BackgroundClearLabel, primaryColor)
                setColor(DateArea.BackgroundMonthAndYearMenuLabels, backgroundColor)
                setColor(DateArea.BackgroundMonthAndYearNavigationButtons, surfaceColor)
                setColor(DateArea.BackgroundCalendarPanelLabelsOnHover, primaryColor)
                setColor(DateArea.BackgroundOverallCalendarPanel, backgroundColor)
                setColor(DateArea.BackgroundTodayLabel, primaryColor)
                setColor(DateArea.BackgroundTopLeftLabelAboveWeekNumbers, primaryColor)
                setColor(DateArea.CalendarBackgroundNormalDates, surfaceColor)
                setColor(DateArea.CalendarBackgroundSelectedDate, primaryColor)
                setColor(DateArea.CalendarBackgroundVetoedDates, Color.lightGray)
                setColor(DateArea.CalendarBorderSelectedDate, primaryColor)
                setColor(DateArea.CalendarDefaultBackgroundHighlightedDates, Color.green)
                setColor(DateArea.CalendarDefaultTextHighlightedDates, onSurfaceColor)
                setColor(DateArea.CalendarTextNormalDates, onSurfaceColor)
                setColor(DateArea.CalendarTextWeekdays, onPrimaryColor)
                setColor(DateArea.CalendarTextWeekNumbers, onSurfaceColor)
                setColor(DateArea.TextClearLabel, onPrimaryColor)
                setColor(DateArea.TextMonthAndYearMenuLabels, onSurfaceColor)
                setColor(DateArea.TextMonthAndYearNavigationButtons, onPrimaryColor)
                setColor(DateArea.TextTodayLabel, onPrimaryColor)
                setColor(DateArea.TextCalendarPanelLabelsOnHover, onSurfaceColor)
                setColor(DateArea.TextFieldBackgroundDisallowedEmptyDate, errorColor)
                setColor(DateArea.TextFieldBackgroundInvalidDate, surfaceColor)
                setColor(DateArea.TextFieldBackgroundValidDate, surfaceColor)
                setColor(DateArea.TextFieldBackgroundVetoedDate, surfaceColor)
                setColor(DateArea.TextFieldBackgroundDisabled, backgroundColor)
                setColor(DateArea.DatePickerTextInvalidDate, errorColor)
                setColor(DateArea.DatePickerTextValidDate, onSurfaceColor)
                setColor(DateArea.DatePickerTextVetoedDate, onSurfaceColor)
                setColor(DateArea.DatePickerTextDisabled, Color(109, 109, 109))
                setColorBackgroundWeekNumberLabels(primaryColor, false)
                setColorBackgroundWeekdayLabels(primaryColor, false)
            },
            TimePickerSettings().apply {
                setColor(TimeArea.TimePickerTextValidTime, onSurfaceColor)
                setColor(TimeArea.TimePickerTextInvalidTime, errorColor)
                setColor(TimeArea.TimePickerTextVetoedTime, onSurfaceColor)
                setColor(TimeArea.TimePickerTextDisabled, Color(109, 109, 109))
                setColor(TimeArea.TextFieldBackgroundValidTime, surfaceColor)
                setColor(TimeArea.TextFieldBackgroundInvalidTime, surfaceColor)
                setColor(TimeArea.TextFieldBackgroundVetoedTime, surfaceColor)
                setColor(TimeArea.TextFieldBackgroundDisallowedEmptyTime, errorColor)
                setColor(TimeArea.TextFieldBackgroundDisabled, backgroundColor)
            }
        ).apply {
            datePicker.componentToggleCalendarButton.applyStyle()
            timePicker.componentToggleTimeMenuButton.applyStyle()

            applyTimePanelColorHack(timePicker)

            dateTimeStrict = dateTime
        }
        add(dateTimePicker)
    }

    /**
     * [TimePicker] is not exposing [TimeMenuPanel] and has no settings to set it's color. Ugh.
     */
    private fun applyTimePanelColorHack(timePicker: TimePicker) {
        if (timeMenuPanelField != null) {
            timePicker.componentToggleTimeMenuButton.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent?) {
                    try {
                        fun JComponent.setColorsRecursive() {
                            background = surfaceColor
                            foreground = onSurfaceColor
                            components.forEach { component ->
                                if (component is JComponent) {
                                    component.setColorsRecursive()
                                }
                            }
                        }

                        val timeMenuPanel = timeMenuPanelField.get(timePicker) as TimeMenuPanel? ?: return
                        timeMenuPanel.setColorsRecursive()
                    } catch (e: Exception) {
                        Napier.e("CalendarPanel nav button style fix failed", e)
                    }
                }
            })
        }
    }

    private fun JButton.applyStyle() {
        background = surfaceColor
    }

    companion object {
        val timeMenuPanelField: Field? = try {
            TimePicker::class.java.getDeclaredField("timeMenuPanel").apply {
                isAccessible = true
            }
        } catch (e: Exception) {
            Napier.e("Failed to get timeMenuPanel field", e)
            null
        }
    }
}
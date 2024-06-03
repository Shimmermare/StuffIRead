package com.shimmermare.stuffiread.ui.components.input.datetime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.shimmermare.stuffiread.i18n.Strings
import com.shimmermare.stuffiread.ui.components.input.DropdownPicker
import com.shimmermare.stuffiread.ui.components.input.ExtendedOutlinedTextField
import com.shimmermare.stuffiread.ui.components.input.SizedIconButton
import com.shimmermare.stuffiread.ui.components.layout.PopupContent
import com.shimmermare.stuffiread.ui.util.remember
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

@Composable
fun DatePicker(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit
) {
    // I implemented this in an hour or so. And I'm not even frontend dev. Why compose devs couldn't implement fucking datepicker?

    var openPopup: Boolean by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        DatePickerField(value, onClick = { openPopup = !openPopup })

        if (openPopup) {
            DatePickerPopup(
                value,
                onDismissRequest = { openPopup = false },
                onValueChange = {
                    onValueChange(it)
                    openPopup = false
                }
            )
        }
    }
}

@Composable
private fun DatePickerField(value: LocalDate, onClick: () -> Unit) {
    ExtendedOutlinedTextField(
        value = "${monthNames[value.month].toString()} ${value.dayOfMonth}, ${value.year}",
        modifier = Modifier.size(210.dp, height = 36.dp).clickable(onClick = onClick),
        singleLine = true,
        // Because readOnly field consumes clicks (aka no open dropdown on click) - use disabled field
        enabled = false,
        // Change disabled field to use default text color
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = LocalContentColor.current.copy(
                LocalContentAlpha.current
            )
        ),
        trailingIcon = { Icon(Icons.Filled.EditCalendar, null) }
    )
}

@Composable
private fun DatePickerPopup(value: LocalDate, onDismissRequest: () -> Unit, onValueChange: (LocalDate) -> Unit) {
    var yearToShow: Int by remember(value) { mutableStateOf(value.year) }
    var monthToShow: Month by remember(value) { mutableStateOf(value.month) }

    val firstDayOfMonth: LocalDate = remember(yearToShow, monthToShow) {
        LocalDate(yearToShow, monthToShow, 1)
    }
    val daysInMonth: Int = remember(firstDayOfMonth) {
        firstDayOfMonth.daysUntil(firstDayOfMonth.plus(1, DateTimeUnit.MONTH))
    }

    Popup(
        focusable = true,
        onDismissRequest = onDismissRequest,
        offset = IntOffset(0, 36)
    ) {
        PopupContent {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.width(272.dp).padding(10.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SizedIconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        size = 36.dp,
                        onClick = {
                            if (monthToShow == Month.JANUARY) {
                                yearToShow--
                            }
                            monthToShow = monthToShow.minus(1)
                        }
                    ) {
                        Icon(Icons.Filled.NavigateBefore, null)
                    }
                    Row {
                        MonthDropdown(monthToShow, onValueChange = { monthToShow = it })
                        YearDropdown(yearToShow, onValueChange = { yearToShow = it })
                    }
                    SizedIconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        size = 36.dp,
                        onClick = {
                            if (monthToShow == Month.DECEMBER) {
                                yearToShow++
                            }
                            monthToShow = monthToShow.plus(1)
                        }
                    ) {
                        Icon(Icons.Filled.NavigateNext, null)
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.width(252.dp)
                ) {
                    items(dayOfWeekShortNames) { DayOfWeekCell(it.remember()) }

                    // If first day of month is not monday - fill offset with empty cells
                    val offset = firstDayOfMonth.dayOfWeek.value - DayOfWeek.MONDAY.value
                    if (offset > 0) {
                        items(offset) { EmptyCell() }
                    }

                    items(daysInMonth) { dayFromZero ->
                        val day = dayFromZero + 1
                        val selected = yearToShow == value.year
                                && monthToShow == value.month
                                && day == value.dayOfMonth
                        DayCell(day, selected) {
                            onValueChange(LocalDate(yearToShow, monthToShow, day))
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MonthDropdown(value: Month, onValueChange: (Month) -> Unit) {
    DropdownPicker(
        value = value,
        onValueChange = onValueChange,
        displayText = { monthNames[it].toString() },
        dropdownValues = monthNames.keys,
    )
}

@Composable
private fun YearDropdown(value: Int, onValueChange: (Int) -> Unit) {
    DropdownPicker(
        value = value,
        onValueChange = onValueChange,
        displayText = { it.toString() }
    ) { _, onPick ->
        LazyColumn(
            state = rememberLazyListState(initialFirstVisibleItemIndex = 95),
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            // 100 years before and 100 years after value
            items(200) { index ->
                val year = value - 100 + index
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .let {
                            if (year == value) it.background(MaterialTheme.colors.primary) else it
                        }
                        .clickable { onPick(year) }
                ) {
                    Text(
                        text = year.toString(),
                        color = if (year == value) MaterialTheme.colors.onPrimary else Color.Unspecified,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayOfWeekCell(shortName: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(36.dp)
    ) {
        Text(
            text = shortName,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6F)
        )
    }
}

@Composable
private fun EmptyCell() {
    Box(
        modifier = Modifier.size(36.dp)
    )
}

@Composable
private fun DayCell(value: Int, selected: Boolean = false, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .let {
                if (selected) {
                    it.background(MaterialTheme.colors.primary, CircleShape)
                } else {
                    it.clickable(
                        onClick = onClick,
                        indication = rememberRipple(bounded = false, radius = 16.dp),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                }
            }
    ) {
        Text(value.toString())
    }
}

private val dayOfWeekShortNames = listOf(
    Strings.components_datePicker_dayOfWeek_short_monday,
    Strings.components_datePicker_dayOfWeek_short_tuesday,
    Strings.components_datePicker_dayOfWeek_short_wednesday,
    Strings.components_datePicker_dayOfWeek_short_thursday,
    Strings.components_datePicker_dayOfWeek_short_friday,
    Strings.components_datePicker_dayOfWeek_short_saturday,
    Strings.components_datePicker_dayOfWeek_short_sunday,
)

private val monthNames = mapOf(
    Month.JANUARY to Strings.components_datePicker_month_january,
    Month.FEBRUARY to Strings.components_datePicker_month_february,
    Month.MARCH to Strings.components_datePicker_month_march,
    Month.APRIL to Strings.components_datePicker_month_april,
    Month.MAY to Strings.components_datePicker_month_may,
    Month.JUNE to Strings.components_datePicker_month_june,
    Month.JULY to Strings.components_datePicker_month_july,
    Month.AUGUST to Strings.components_datePicker_month_august,
    Month.SEPTEMBER to Strings.components_datePicker_month_september,
    Month.OCTOBER to Strings.components_datePicker_month_october,
    Month.NOVEMBER to Strings.components_datePicker_month_november,
    Month.DECEMBER to Strings.components_datePicker_month_december,
)
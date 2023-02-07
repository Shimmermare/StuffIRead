package com.shimmermare.stuffiread.ui.components.table

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * MaterialUI-like table implementation.
 * Because Compose doesn't have one (!!!).
 *
 * @param defaultOrder custom comparator for default ordering. Otherwise, default order will be from [items].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> Table(
    items: Collection<T>,
    defaultOrder: Comparator<T> = Comparator { _, _ -> 0 },
    modifier: Modifier = Modifier,
    onRowClick: ((T) -> Unit)? = null,
    columnDefs: TableDsl<T>.() -> Unit
) {
    val columns: List<TableColumn<T>> = remember { TableDsl<T>().also { columnDefs.invoke(it) }.getDefinitions() }
    val lazyColumnState = rememberLazyListState()

    var sortColumn: Int? by remember { mutableStateOf(null) }
    var sortInverse: Boolean by remember { mutableStateOf(false) }
    val sortedItems: List<T> = remember(items, sortColumn, sortInverse) {
        items.sortedWith(
            if (sortColumn != null) {
                val sorter =
                    columns[sortColumn!!].sorter ?: throw IllegalStateException("Unsortable column: $sortColumn")
                if (sortInverse) sorter.reversed() else sorter
            } else {
                defaultOrder
            }
        )
    }

    Box(
        modifier = modifier
    ) {
        LazyColumn(
            state = lazyColumnState, modifier = Modifier.padding(end = 12.dp)
        ) {
            stickyHeader {
                TableHeader(columns, sortColumn, sortInverse, onSortToggle = {
                    when {
                        sortColumn == it && !sortInverse -> {
                            sortInverse = true
                        }

                        sortColumn == it && sortInverse -> {
                            sortColumn = null
                            sortInverse = false
                        }

                        else -> {
                            sortColumn = it
                            sortInverse = false
                        }
                    }
                })
            }
            itemsIndexed(sortedItems) { index, item ->
                TableRow(index, item, columns, onRowClick)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyColumnState)
        )
    }
}

@Composable
private fun <T> TableHeader(
    columns: List<TableColumn<T>>, sortColumn: Int?, sortInverse: Boolean, onSortToggle: (Int) -> Unit
) {
    Row(modifier = Modifier.background(color = MaterialTheme.colors.background)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEach { column ->
                ColumnHeader(column = column,
                    sortedBy = column.index == sortColumn,
                    sortInverse = sortInverse,
                    onSortToggle = { onSortToggle.invoke(column.index) })
            }
        }
    }
    Divider()
}

@Composable
private fun <T> RowScope.ColumnHeader(
    column: TableColumn<T>, sortedBy: Boolean, sortInverse: Boolean, onSortToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .let { if (column.columnWeight == null) it else it.weight(column.columnWeight) }
            .let { if (column.sorter == null) it else it.clickable { onSortToggle() } },
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(vertical = 2.5.dp)
        ) {
            if (column.sorter != null) {
                Box(
                    modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart
                ) {
                    column.header.invoke()

                    if (!column.hideSortIcon) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            column.header.invoke()

                            if (sortedBy) {
                                var modifier: Modifier = Modifier
                                if (sortInverse) {
                                    modifier = Modifier.scale(1F, -1F)
                                }
                                Icon(Icons.Filled.Sort, null, modifier = modifier)
                            } else {
                                Icon(Icons.Filled.FilterList, null, modifier = Modifier.rotate(90F))
                            }
                        }
                    }
                }
            } else {
                column.header.invoke()
            }
        }
    }
}

@Composable
private fun <T> TableRow(index: Int, item: T, columns: List<TableColumn<T>>, onRowClick: ((T) -> Unit)?) {
    Row(
        modifier = Modifier.let { if (onRowClick == null) it else it.clickable { onRowClick(item) } },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        columns.forEach { column ->
            Column(modifier = Modifier.fillMaxHeight().padding(vertical = 2.5.dp)
                .let { if (column.columnWeight == null) it else it.weight(column.columnWeight) },
                verticalArrangement = Arrangement.Center
            ) {
                column.cell.invoke(index, item)
            }
        }
    }
    Divider()
}

class TableDsl<T> {
    private val columns = mutableListOf<TableColumn<T>>()

    fun column(
        title: String,
        sorter: Comparator<T>? = null,
        hideSortIcon: Boolean = false,
        columnWeight: Float? = null,
        cell: @Composable (item: T) -> Unit
    ) {
        column(title, sorter, hideSortIcon, columnWeight) { _, item -> cell.invoke(item) }
    }

    fun column(
        title: String,
        sorter: Comparator<T>? = null,
        hideSortIcon: Boolean = false,
        columnWeight: Float? = null,
        cell: @Composable (index: Int, item: T) -> Unit
    ) {
        column(header = { Text(text = title, fontWeight = FontWeight.Bold) }, sorter, hideSortIcon, columnWeight, cell)
    }

    fun column(
        header: @Composable () -> Unit,
        sorter: Comparator<T>? = null,
        hideSortIcon: Boolean = false,
        columnWeight: Float? = null,
        cell: @Composable (item: T) -> Unit
    ) {
        column(header, sorter, hideSortIcon, columnWeight) { _, item -> cell.invoke(item) }
    }

    fun column(
        header: @Composable () -> Unit,
        sorter: Comparator<T>? = null,
        hideSortIcon: Boolean = false,
        columnWeight: Float? = null,
        cell: @Composable (index: Int, item: T) -> Unit
    ) {
        columns.add(TableColumn(columns.size, header, sorter, hideSortIcon, columnWeight, cell))
    }

    fun getDefinitions(): List<TableColumn<T>> = columns.toList()
}

data class TableColumn<T>(
    val index: Int,
    val header: @Composable () -> Unit,
    val sorter: Comparator<T>? = null,
    val hideSortIcon: Boolean = false,
    val columnWeight: Float?,
    val cell: @Composable (index: Int, item: T) -> Unit,
)
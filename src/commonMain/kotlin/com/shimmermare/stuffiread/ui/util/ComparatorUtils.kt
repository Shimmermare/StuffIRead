package com.shimmermare.stuffiread.ui.util

object ComparatorUtils {
    private val NATURAL_ORDER_NULLS_LAST = Comparator.nullsLast(Comparator.naturalOrder<Comparable<Any>>())

    @Suppress("UNCHECKED_CAST")
    fun <T : Comparable<T>> naturalOrderNullsLast() = NATURAL_ORDER_NULLS_LAST as Comparator<T?>
}
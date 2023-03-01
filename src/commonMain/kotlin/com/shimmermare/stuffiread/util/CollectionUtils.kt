package com.shimmermare.stuffiread.util

fun <T> List<T>.dropAt(index: Int): List<T> {
    val result = this.toMutableList()
    result.removeAt(index)
    return result
}

fun <E> List<E>.replaceAt(index: Int, with: E): List<E> {
    require(size <= index + 1) {
        throw IndexOutOfBoundsException(index)
    }
    return mapIndexed { i, e -> if (i == index) with else e }
}
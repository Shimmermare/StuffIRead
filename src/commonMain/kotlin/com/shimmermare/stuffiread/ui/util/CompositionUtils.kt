package com.shimmermare.stuffiread.ui.util

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

inline fun <reified T> staticCompositionLocalOrThrow() = staticCompositionLocalOrThrow<T>(T::class.simpleName!!)

fun <T> staticCompositionLocalOrThrow(name: String) = staticCompositionLocalOf<T> {
    throw IllegalStateException("Local $name is not set")
}

inline fun <reified T> compositionLocalOrThrow() = compositionLocalOrThrow<T>(T::class.simpleName!!)

fun <T> compositionLocalOrThrow(name: String) = compositionLocalOf<T> {
    throw IllegalStateException("Local $name is not set")
}
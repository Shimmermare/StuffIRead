package com.shimmermare.stuffiread.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.unit.DpSize

val LocalWindowSize: ProvidableCompositionLocal<DpSize> = compositionLocalOrThrow()

val windowSize @Composable get() = LocalWindowSize.current
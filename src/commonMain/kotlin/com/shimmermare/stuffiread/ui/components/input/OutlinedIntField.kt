package com.shimmermare.stuffiread.ui.components.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * @param range inclusive range
 */
@Composable
fun OutlinedIntField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
) {
    ExtendedOutlinedTextField(
        value = value.toString(),
        modifier = modifier,
        isError = isError,
        singleLine = true,
        onValueChange = {
            val negative = it.startsWith('-')
            val onlyDigits = it.filter { c -> c.isDigit() }.let { s ->
                when {
                    s.isEmpty() -> "0"
                    negative -> "-$s"
                    else -> s
                }
            }
            val intValue = onlyDigits.toBigInteger()
                .max(range.first.toBigInteger())
                .min(range.last.toBigInteger())
                .intValueExact()
            if (value != intValue) onValueChange(intValue)
        }
    )
}

/**
 * @param range inclusive range
 */
@Composable
fun OutlinedUIntField(
    value: UInt,
    onValueChange: (UInt) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    range: UIntRange = UInt.MIN_VALUE..UInt.MAX_VALUE,
) {
    ExtendedOutlinedTextField(
        value = value.toString(),
        modifier = modifier,
        isError = isError,
        singleLine = true,
        onValueChange = {
            val onlyDigits = it.filter { c -> c.isDigit() }.let { s ->
                when {
                    s.isEmpty() -> "0"
                    else -> s
                }
            }
            val uintValue = onlyDigits.toBigInteger()
                .max(range.first.toLong().toBigInteger())
                .min(range.last.toLong().toBigInteger())
                .longValueExact()
                .toUInt()
            if (value != uintValue) onValueChange(uintValue)
        }
    )
}
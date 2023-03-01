package com.shimmermare.stuffiread.ui.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HumanReadableBytesUtilsTest {
    @Test
    fun `Long#toHumanReadableBytes`() {
        assertEquals("5 kB", 5000L.toHumanReadableBytes())
        assertEquals("4.8 KiB", 5000L.toHumanReadableBytes(base2 = true))
        assertEquals("4.882812 KiB", 5000L.toHumanReadableBytes(base2 = true, precision = 6))

        assertEquals("999.9 kB", 999999L.toHumanReadableBytes())
        assertEquals("1 MB", 1000000L.toHumanReadableBytes())

        assertEquals("9.223372036 EB", Long.MAX_VALUE.toHumanReadableBytes(precision = 9))
        assertEquals("8 EiB", Long.MAX_VALUE.toHumanReadableBytes(base2 = true, precision = 9))

        assertFailsWith<IllegalArgumentException> { (-1L).toHumanReadableBytes() }
    }
}

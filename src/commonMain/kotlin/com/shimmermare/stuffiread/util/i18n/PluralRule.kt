package com.shimmermare.stuffiread.util.i18n

import de.comahe.i18n4k.Locale

expect class PluralRule {
    fun select(value: Number): PluralCategory

    companion object {
        fun createOrDefault(locale: Locale): PluralRule
    }
}
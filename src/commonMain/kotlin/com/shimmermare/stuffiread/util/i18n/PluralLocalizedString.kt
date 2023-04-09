package com.shimmermare.stuffiread.util.i18n

/**
 * To be used with [de.comahe.i18n4k.strings.LocalizedString] or similar to select correct localized string
 * CLDR form based on number value. See https://cldr.unicode.org/index/cldr-spec/plural-rules
 */
data class PluralLocalizedString<T>(
    private val zero: T,
    private val one: T,
    private val two: T,
    private val few: T,
    private val many: T,
    private val other: T,
) {
    fun select(rules: PluralRule, count: Number): T {
        return when (rules.select(count)) {
            PluralCategory.OTHER -> other
            PluralCategory.ZERO -> zero
            PluralCategory.ONE -> one
            PluralCategory.TWO -> two
            PluralCategory.FEW -> few
            PluralCategory.MANY -> many
            else -> other
        }
    }
}
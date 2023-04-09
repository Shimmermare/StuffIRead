package com.shimmermare.stuffiread.util.i18n

import de.comahe.i18n4k.Locale
import net.xyzsd.plurals.PluralRuleType

private typealias InternalPluralRule = net.xyzsd.plurals.PluralRule

actual class PluralRule(private val rule: InternalPluralRule) {
    actual fun select(value: Number): PluralCategory {
        return rule.select(value)
    }

    actual companion object {
        actual fun createOrDefault(locale: Locale): PluralRule {
            return PluralRule(InternalPluralRule.createOrDefault(locale, PluralRuleType.CARDINAL))
        }
    }
}
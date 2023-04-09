package com.shimmermare.stuffiread.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shimmermare.stuffiread.ui.CurrentLocale
import com.shimmermare.stuffiread.util.i18n.PluralLocalizedString
import com.shimmermare.stuffiread.util.i18n.PluralRule
import de.comahe.i18n4k.Locale
import de.comahe.i18n4k.strings.LocalizedString
import de.comahe.i18n4k.strings.LocalizedStringFactory1
import de.comahe.i18n4k.strings.LocalizedStringFactory2
import de.comahe.i18n4k.strings.LocalizedStringFactory3
import de.comahe.i18n4k.strings.LocalizedStringFactory4
import de.comahe.i18n4k.strings.LocalizedStringFactory5

/**
 * Will be recomposed on current locale change.
 */
@Composable
fun LocalizedString.remember(): String {
    return remember(CurrentLocale) { toString() }
}

@Composable
fun LocalizedStringFactory1.remember(arg0: Any): String {
    return remember(CurrentLocale, arg0) { this(arg0) }
}

@Composable
fun LocalizedStringFactory2.remember(arg0: Any, arg1: Any): String {
    return remember(CurrentLocale, arg0, arg1) { this(arg0, arg1) }
}

@Composable
fun LocalizedStringFactory3.remember(arg0: Any, arg1: Any, arg2: Any): String {
    return remember(CurrentLocale, arg0, arg1, arg2) { this(arg0, arg1, arg2) }
}

@Composable
fun LocalizedStringFactory4.remember(arg0: Any, arg1: Any, arg2: Any, arg3: Any): String {
    return remember(CurrentLocale, arg0, arg1, arg2, arg3) { this(arg0, arg1, arg2, arg3) }
}

@Composable
fun LocalizedStringFactory5.remember(arg0: Any, arg1: Any, arg2: Any, arg3: Any, arg4: Any): String {
    return remember(CurrentLocale, arg0, arg1, arg2, arg3, arg4) { this(arg0, arg1, arg2, arg3, arg4) }
}

@Composable
@JvmName("rememberLocalizedString")
fun PluralLocalizedString<LocalizedString>.remember(count: Number): String {
    return remember(CurrentLocale, count) { this.select(CurrentPluralRulesHolder.getCurrent(), count).toString() }
}

@Composable
@JvmName("rememberLocalizedStringFactory1")
fun PluralLocalizedString<LocalizedStringFactory1>.remember(count: Number): String {
    return remember(CurrentLocale, count) { this.select(CurrentPluralRulesHolder.getCurrent(), count).invoke(count) }
}

@Composable
@JvmName("rememberLocalizedStringFactory2")
fun PluralLocalizedString<LocalizedStringFactory2>.remember(count: Number, arg1: Any): String {
    return remember(CurrentLocale, count, arg1) {
        this.select(CurrentPluralRulesHolder.getCurrent(), count).invoke(count, arg1)
    }
}

@Composable
@JvmName("rememberLocalizedStringFactory3")
fun PluralLocalizedString<LocalizedStringFactory3>.remember(count: Number, arg1: Any, arg2: Any): String {
    return remember(CurrentLocale, count, arg1, arg2) {
        this.select(CurrentPluralRulesHolder.getCurrent(), count).invoke(count, arg1, arg2)
    }
}

@Composable
@JvmName("rememberLocalizedStringFactory4")
fun PluralLocalizedString<LocalizedStringFactory4>.remember(count: Number, arg1: Any, arg2: Any, arg3: Any): String {
    return remember(CurrentLocale, count, arg1, arg2, arg3) {
        this.select(CurrentPluralRulesHolder.getCurrent(), count).invoke(count, arg1, arg2, arg3)
    }
}

@Composable
@JvmName("rememberLocalizedStringFactory5")
fun PluralLocalizedString<LocalizedStringFactory5>.remember(count: Number, arg1: Any, arg2: Any, arg3: Any, arg4: Any): String {
    return remember(CurrentLocale, count, arg1, arg2, arg3, arg4) {
        this.select(CurrentPluralRulesHolder.getCurrent(), count).invoke(count, arg1, arg2, arg3, arg4)
    }
}

private object CurrentPluralRulesHolder {
    private var rulesLocale: Locale = CurrentLocale
    private var rules: PluralRule? = null

    fun getCurrent(): PluralRule {
        if (rules == null || CurrentLocale != rulesLocale) {
            rulesLocale = CurrentLocale
            rules = PluralRule.createOrDefault(CurrentLocale)
        }
        return rules!!
    }
}
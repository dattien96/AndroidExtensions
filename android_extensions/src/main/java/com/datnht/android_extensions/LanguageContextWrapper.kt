package com.datnht.android_extensions

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class LanguageContextWrapper constructor(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(
            context: Context,
            newLocale: Locale?
        ): LanguageContextWrapper? {
            var context = context
            val res = context.resources
            val configuration = res.configuration
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    configuration.setLocale(newLocale)
                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)
                    context = context.createConfigurationContext(configuration)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> {
                    configuration.setLocale(newLocale)
                    context = context.createConfigurationContext(configuration)
                }
                else -> {
                    configuration.locale = newLocale
                    res.updateConfiguration(configuration, res.displayMetrics)
                }
            }
            return LanguageContextWrapper(context)
        }
    }
}

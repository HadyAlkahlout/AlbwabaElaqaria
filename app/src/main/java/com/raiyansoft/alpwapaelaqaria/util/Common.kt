package com.raiyansoft.alpwapaelaqaria.util

import android.content.Context
import android.content.res.Configuration
import java.util.*

object Common {
    fun setLocale(lang: String, context: Context) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources
            .updateConfiguration(config, context.resources.displayMetrics)
    }
}
package com.lovely.bakingrecipes.ui.theme

import android.content.Context

enum class ThemeMode { SYSTEM, LIGHT, DARK }

// Simple persisted app settings backed by SharedPreferences.
object ThemePreferences {
    private const val PREFS = "settings"
    private const val KEY = "theme_mode"

    fun load(context: Context): ThemeMode {
        val name = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return runCatching { ThemeMode.valueOf(name) }.getOrDefault(ThemeMode.SYSTEM)
    }

    fun save(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, mode.name)
            .apply()
    }
}

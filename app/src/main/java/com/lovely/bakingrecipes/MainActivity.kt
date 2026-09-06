package com.lovely.bakingrecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.lovely.bakingrecipes.navigation.AppNavigation
import com.lovely.bakingrecipes.ui.theme.BakingRecipesTheme
import com.lovely.bakingrecipes.ui.theme.ThemeMode
import com.lovely.bakingrecipes.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var themeMode by rememberSaveable { mutableStateOf(ThemePreferences.load(context)) }
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            BakingRecipesTheme(darkTheme = darkTheme) {
                AppNavigation(
                    themeMode = themeMode,
                    onThemeModeChange = {
                        themeMode = it
                        ThemePreferences.save(context, it)
                    }
                )
            }
        }
    }
}
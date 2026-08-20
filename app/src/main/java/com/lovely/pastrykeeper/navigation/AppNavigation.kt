package com.lovely.pastrykeeper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lovely.pastrykeeper.ui.screens.add.AddPastryScreen
import com.lovely.pastrykeeper.ui.screens.home.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                onAddPastryClick = {
                    navController.navigate("add_pastry")
                }
            )
        }

        composable("add_pastry") {
            AddPastryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
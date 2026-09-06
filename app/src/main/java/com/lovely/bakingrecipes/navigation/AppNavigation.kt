package com.lovely.bakingrecipes.navigation

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lovely.bakingrecipes.data.PastryDatabase
import com.lovely.bakingrecipes.repository.PastryRepository
import com.lovely.bakingrecipes.ui.screens.add.AddPastryScreen
import com.lovely.bakingrecipes.ui.screens.detail.PastryDetailScreen
import com.lovely.bakingrecipes.ui.screens.home.HomeScreen
import com.lovely.bakingrecipes.ui.screens.ingredients.IngredientsListScreen
import com.lovely.bakingrecipes.ui.screens.overview.OverviewScreen
import com.lovely.bakingrecipes.ui.screens.pastrylist.PastryListScreen
import com.lovely.bakingrecipes.ui.screens.settings.SettingsScreen
import com.lovely.bakingrecipes.ui.theme.ThemeMode
import com.lovely.bakingrecipes.viewmodel.AddPastryViewModel
import com.lovely.bakingrecipes.viewmodel.AddPastryViewModelFactory
import com.lovely.bakingrecipes.viewmodel.DashboardTarget
import com.lovely.bakingrecipes.viewmodel.GenericViewModelFactory
import com.lovely.bakingrecipes.viewmodel.HomeViewModel
import com.lovely.bakingrecipes.viewmodel.HomeViewModelFactory
import com.lovely.bakingrecipes.viewmodel.IngredientsListViewModel
import com.lovely.bakingrecipes.viewmodel.OverviewViewModel
import com.lovely.bakingrecipes.viewmodel.PastryDetailViewModel
import com.lovely.bakingrecipes.viewmodel.PastryDetailViewModelFactory
import com.lovely.bakingrecipes.viewmodel.PastryListViewModel

// Maps a tapped dashboard tile to its destination route.
private fun targetRoute(target: DashboardTarget): String = when (target) {
    DashboardTarget.Ingredients -> "ingredients"
    DashboardTarget.AllPastries -> "pastry_list/__all__"
    is DashboardTarget.Category -> "pastry_list/${Uri.encode(target.name)}"
}

@Composable
fun AppNavigation(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {

    val navController = rememberNavController()

    val context = LocalContext.current
    val application = context.applicationContext as Application

    val database = PastryDatabase.getDatabase(context)

    val repository = PastryRepository(
        pastryDao = database.pastryDao()
    )

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") { backStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )
            val uiState by homeViewModel.uiState.collectAsState()
            val addedMessage by backStackEntry.savedStateHandle
                .getStateFlow<String?>("added_pastry_message", null)
                .collectAsState()
            val deletedMessage by backStackEntry.savedStateHandle
                .getStateFlow<String?>("deleted_recipe_name", null)
                .collectAsState()
            HomeScreen(
                uiState = uiState,
                onSearchChange = homeViewModel::onSearchChange,
                onCategorySelected = homeViewModel::onCategorySelected,
                onAddPastryClick = {
                    navController.navigate("add_pastry")
                },
                onPastryClick = { pastryId ->
                    navController.navigate("detail/$pastryId")
                },
                onTileClick = { target ->
                    navController.navigate(targetRoute(target))
                },
                onSeeAllClick = {
                    navController.navigate("overview")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                addedMessage = addedMessage,
                onAddedMessageShown = {
                    backStackEntry.savedStateHandle["added_pastry_message"] = null
                },
                deletedMessage = deletedMessage,
                onDeletedMessageShown = {
                    backStackEntry.savedStateHandle["deleted_recipe_name"] = null
                }
            )
        }

        composable("add_pastry") {
            val viewModel: AddPastryViewModel = viewModel(
                factory = AddPastryViewModelFactory(application, repository, null)
            )
            AddPastryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = { _, name, _ ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("added_pastry_message", "$name added")
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(
            route = "edit_pastry/{pastryId}",
            arguments = listOf(navArgument("pastryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pastryId = backStackEntry.arguments?.getInt("pastryId") ?: return@composable
            val viewModel: AddPastryViewModel = viewModel(
                factory = AddPastryViewModelFactory(application, repository, pastryId)
            )
            AddPastryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveComplete = { _, name, _ ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("edited_pastry_name", name)
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(
            route = "detail/{pastryId}",
            arguments = listOf(navArgument("pastryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pastryId = backStackEntry.arguments?.getInt("pastryId") ?: return@composable
            val detailViewModel: PastryDetailViewModel = viewModel(
                factory = PastryDetailViewModelFactory(application, repository, pastryId)
            )
            val pastry by detailViewModel.pastry.collectAsState()
            val editedName by backStackEntry.savedStateHandle
                .getStateFlow<String?>("edited_pastry_name", null)
                .collectAsState()
            PastryDetailScreen(
                pastry = pastry,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = { id ->
                    navController.navigate("edit_pastry/$id")
                },
                onDeleteConfirmed = { toDelete ->
                    detailViewModel.deletePastry(toDelete)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("deleted_recipe_name", toDelete.name)
                    navController.popBackStack()
                },
                editedMessage = editedName,
                onEditedMessageShown = {
                    backStackEntry.savedStateHandle["edited_pastry_name"] = null
                }
            )
        }

        composable("overview") {
            val overviewViewModel: OverviewViewModel = viewModel(
                factory = GenericViewModelFactory { OverviewViewModel(repository) }
            )
            val tiles by overviewViewModel.tiles.collectAsState()
            OverviewScreen(
                tiles = tiles,
                onBackClick = {
                    navController.popBackStack()
                },
                onTileClick = { target ->
                    navController.navigate(targetRoute(target))
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("ingredients") { backStackEntry ->
            val ingredientsViewModel: IngredientsListViewModel = viewModel(
                factory = GenericViewModelFactory { IngredientsListViewModel(repository) }
            )
            val items by ingredientsViewModel.groups.collectAsState()
            val ingredientSearch by ingredientsViewModel.searchQuery.collectAsState()
            val ingredientsLoading by ingredientsViewModel.isLoading.collectAsState()
            val ingredientsDeletedMessage by backStackEntry.savedStateHandle
                .getStateFlow<String?>("deleted_recipe_name", null)
                .collectAsState()
            IngredientsListScreen(
                groups = items,
                isLoading = ingredientsLoading,
                searchQuery = ingredientSearch,
                onSearchChange = ingredientsViewModel::onSearchChange,
                onBackClick = {
                    navController.popBackStack()
                },
                onPastryClick = { id ->
                    navController.navigate("detail/$id")
                },
                deletedMessage = ingredientsDeletedMessage,
                onDeletedMessageShown = {
                    backStackEntry.savedStateHandle["deleted_recipe_name"] = null
                }
            )
        }

        composable(
            route = "pastry_list/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("category")
            val category = if (raw == null || raw == "__all__") null else raw
            val listViewModel: PastryListViewModel = viewModel(
                factory = GenericViewModelFactory { PastryListViewModel(repository, category) }
            )
            val pastries by listViewModel.pastries.collectAsState()
            val listLoading by listViewModel.isLoading.collectAsState()
            val listDeletedMessage by backStackEntry.savedStateHandle
                .getStateFlow<String?>("deleted_recipe_name", null)
                .collectAsState()
            PastryListScreen(
                title = listViewModel.title,
                pastries = pastries,
                isLoading = listLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onPastryClick = { id ->
                    navController.navigate("detail/$id")
                },
                deletedMessage = listDeletedMessage,
                onDeletedMessageShown = {
                    backStackEntry.savedStateHandle["deleted_recipe_name"] = null
                }
            )
        }
    }
}
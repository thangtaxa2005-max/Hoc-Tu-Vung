package com.example.hoctuvung.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hoctuvung.ui.add.AddBulkScreen
import com.example.hoctuvung.ui.add.AddWordScreen
import com.example.hoctuvung.ui.home.HomeScreen
import com.example.hoctuvung.ui.library.LibraryScreen
import com.example.hoctuvung.ui.review.ReviewScreen
import com.example.hoctuvung.ui.review.ReviewViewModel
import com.example.hoctuvung.ui.settings.SettingsScreen
import com.example.hoctuvung.ui.stats.StatsScreen

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Filled.Home),
    BottomItem(Routes.LIBRARY, "Library", Icons.AutoMirrored.Filled.List),
    BottomItem(Routes.STATS, "Stats", Icons.Filled.Info),
    BottomItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                val context = LocalContext.current
                val homeViewModel: com.example.hoctuvung.ui.home.HomeViewModel = viewModel(factory = com.example.hoctuvung.ui.home.HomeViewModel.factory(context))
                HomeScreen(
                    viewModel = homeViewModel,
                    onStartRecall = { navController.navigate(Routes.review("recall")) },
                    onStartCloze = { navController.navigate(Routes.review("cloze")) },
                    onStartListening = { navController.navigate(Routes.review("listening")) }
                )
            }
            composable(Routes.LIBRARY) {
                LibraryScreen(onAddClick = { navController.navigate(Routes.ADD) })
            }
            composable(Routes.ADD) {
                AddWordScreen(onBulkClick = { navController.navigate(Routes.ADD_BULK) })
            }
            composable(Routes.ADD_BULK) { AddBulkScreen() }
            composable(Routes.STATS) {
                val context = LocalContext.current
                val statsViewModel: com.example.hoctuvung.ui.stats.StatsViewModel = viewModel(factory = com.example.hoctuvung.ui.stats.StatsViewModel.factory(context))
                StatsScreen(viewModel = statsViewModel)
            }
            composable(Routes.SETTINGS) {
                val context = LocalContext.current
                val settingsViewModel: com.example.hoctuvung.ui.settings.SettingsViewModel = viewModel(factory = com.example.hoctuvung.ui.settings.SettingsViewModel.factory(context))
                SettingsScreen(viewModel = settingsViewModel)
            }
            composable(
                route = Routes.REVIEW,
                arguments = listOf(navArgument("mode") { type = NavType.StringType })
            ) { backStackEntryReview ->
                val mode = backStackEntryReview.arguments?.getString("mode") ?: "recall"
                when (mode) {
                    "recall", "cloze", "listening" -> {
                        val context = LocalContext.current
                        val reviewViewModel: ReviewViewModel = viewModel(
                            factory = ReviewViewModel.factory(context, mode)
                        )
                        ReviewScreen(
                            viewModel = reviewViewModel,
                            onFinished = { navController.popBackStack() }
                        )
                    }
                    else -> {
                        Text("Chế độ '$mode' chưa làm (Task 7/8)")
                    }
                }
            }
        }
    }
}

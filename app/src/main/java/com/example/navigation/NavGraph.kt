package com.example.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.screens.*
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.ConvertersViewModel

@Composable
fun AppNavigation(
    calculatorViewModel: CalculatorViewModel,
    convertersViewModel: ConvertersViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tabs for our primary navigation
    val mainTabs = remember {
        listOf(
            Triple(Screen.Calculator, Icons.Default.Calculate, "Calculator"),
            Triple(Screen.History, Icons.Default.History, "History"),
            Triple(Screen.ExtraTools, Icons.Default.GridView, "Tools"),
            Triple(Screen.Settings, Icons.Default.Settings, "Settings")
        )
    }

    // Determine whether to display the bottom navigation bar
    val showBottomBar = currentRoute in listOf(
        Screen.Calculator.route,
        Screen.History.route,
        Screen.ExtraTools.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    mainTabs.forEach { (screen, icon, label) ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = icon, contentDescription = label) },
                            label = { Text(text = label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main views
            composable(Screen.Calculator.route) {
                CalculatorScreen(viewModel = calculatorViewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(viewModel = calculatorViewModel)
            }
            composable(Screen.ExtraTools.route) {
                ExtraToolsScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = calculatorViewModel)
            }

            // Sub-converter views
            composable(Screen.UnitConverter.route) {
                UnitConverterScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.CurrencyConverter.route) {
                CurrencyConverterScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.BmiCalculator.route) {
                BmiCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.AgeCalculator.route) {
                AgeCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.PercentageCalculator.route) {
                PercentageCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.GstCalculator.route) {
                GstCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.DiscountCalculator.route) {
                DiscountCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.EmiCalculator.route) {
                EmiCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
            composable(Screen.TipCalculator.route) {
                TipCalculatorScreen(viewModel = convertersViewModel, navController = navController)
            }
        }
    }
}

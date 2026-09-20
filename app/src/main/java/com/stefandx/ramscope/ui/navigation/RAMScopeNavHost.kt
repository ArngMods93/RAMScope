package com.stefandx.ramscope.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Memory
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stefandx.ramscope.data.MemoryInfoProvider
import com.stefandx.ramscope.domain.GetMemorySnapshotUseCase
import com.stefandx.ramscope.ui.dashboard.DashboardScreen
import com.stefandx.ramscope.ui.dashboard.DashboardViewModel
import com.stefandx.ramscope.ui.details.MemoryDetailsScreen
import com.stefandx.ramscope.ui.processes.ProcessesScreen
import com.stefandx.ramscope.ui.processes.ProcessesViewModel

private sealed class Destination(val route: String, val label: String) {
    data object Dashboard : Destination("dashboard", "Dashboard")
    data object Details : Destination("details", "Details")
    data object Processes : Destination("processes", "Processes")
}

private val bottomBarDestinations = listOf(Destination.Dashboard, Destination.Details, Destination.Processes)

/**
 * Root composable: wires up navigation and creates the (very small) view
 * model graph by hand — the app has too few screens to justify pulling in
 * a DI framework.
 */
@Composable
fun RAMScopeNavHost(memoryInfoProvider: MemoryInfoProvider) {
    val navController = rememberNavController()
    val getMemorySnapshot = GetMemorySnapshotUseCase(memoryInfoProvider)
    val dashboardViewModel = DashboardViewModel(getMemorySnapshot)
    val processesViewModel = ProcessesViewModel(memoryInfoProvider)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                bottomBarDestinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            val icon = when (destination) {
                                Destination.Dashboard -> Icons.Default.Dashboard
                                Destination.Details -> Icons.Default.Memory
                                Destination.Processes -> Icons.Default.List
                            }
                            Icon(icon, contentDescription = destination.label)
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destination.Dashboard.route) { DashboardScreen(dashboardViewModel) }
            composable(Destination.Details.route) { MemoryDetailsScreen(dashboardViewModel) }
            composable(Destination.Processes.route) { ProcessesScreen(processesViewModel) }
        }
    }
}

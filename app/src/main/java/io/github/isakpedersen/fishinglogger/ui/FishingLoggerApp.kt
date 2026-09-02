package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

private data class NavigationTab(val route: Any, val label: String, val icon: ImageVector)

private val navigationTabs = listOf(
    NavigationTab(route = CatchList, label = "Fangster", icon = Icons.AutoMirrored.Filled.List),
    NavigationTab(route = LureCatalog, label = "Slukkatalog", icon = Icons.Default.Star),
)

@Composable
fun FishingLoggerApp() {
    val watchSyncViewModel: WatchSyncViewModel =
        viewModel(factory = WatchSyncViewModel.Factory)

    val status by watchSyncViewModel.status.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        watchSyncViewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()

    FishingLoggerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    navigationTabs.forEach { tab ->
                        val selected = currentEntry?.destination?.hasRoute(tab.route::class) == true
                        NavigationBarItem(
                            selected = selected,
                            icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                            onClick = {
                                if (!selected) {
                                    navController.navigate(tab.route) {
                                        popUpTo(id = navController.graph.findStartDestination().id)
                                        launchSingleTop = true
                                    }
                                }
                            },
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                StatusScreen(status = status)
                NavHost(
                    navController = navController,
                    startDestination = CatchList,
                    modifier = Modifier.weight(1f),
                ) {
                    composable<CatchList> {
                        val vm: CatchListViewModel =
                            viewModel(factory = CatchListViewModel.Factory)
                        LaunchedEffect(Unit) {
                            vm.events.collect { message ->
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                        val catches by vm.catches.collectAsStateWithLifecycle(initialValue = null)
                        CatchListScreen(
                            catches = catches,
                            onCatchClick = { id ->
                                navController.navigate(CatchDetail(id))
                            },
                            onDeleteCatch = vm::deleteCatch,
                        )
                    }

                    composable<CatchDetail> {
                        val vm: CatchDetailViewModel =
                            viewModel(factory = CatchDetailViewModel.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle(
                            initialValue = null,
                        )
                        CatchDetailScreen(uiState = uiState, onBack = { navController.navigateUp() })
                    }

                    composable<LureCatalog> {
                        val vm: LureCatalogViewModel =
                            viewModel(factory = LureCatalogViewModel.Factory)
                        LaunchedEffect(Unit) {
                            vm.events.collect { message ->
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                        val catalog by vm.catalog.collectAsStateWithLifecycle(
                            initialValue = emptyList(),
                        )
                        val expandedModelIds by vm.expandedModelIds.collectAsStateWithLifecycle()
                        LureCatalogScreen(
                            catalog = catalog,
                            expandedModelIds = expandedModelIds,
                            onModelClick = vm::toggleModelExpanded,
                            onSaveModel = vm::addModel,
                            onSaveVariant = vm::addVariant,
                            onDeleteModel = vm::deleteModel,
                            onDeleteVariant = vm::deleteVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusScreen(status: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text("Status: $status")
    }
}

@Preview(showBackground = true)
@Composable
fun StatusScreenPreview() {
    FishingLoggerTheme {
        StatusScreen("SDK ready")
    }
}

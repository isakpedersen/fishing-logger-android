package io.github.isakpedersen.fishinglogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.isakpedersen.fishinglogger.ui.CatchDetail
import io.github.isakpedersen.fishinglogger.ui.CatchDetailScreen
import io.github.isakpedersen.fishinglogger.ui.CatchDetailUiState
import io.github.isakpedersen.fishinglogger.ui.CatchDetailViewModel
import io.github.isakpedersen.fishinglogger.ui.CatchList
import io.github.isakpedersen.fishinglogger.ui.CatchListScreen
import io.github.isakpedersen.fishinglogger.ui.CatchListViewModel
import io.github.isakpedersen.fishinglogger.ui.LureCatalog
import io.github.isakpedersen.fishinglogger.ui.LureCatalogScreen
import io.github.isakpedersen.fishinglogger.ui.LureCatalogViewModel
import io.github.isakpedersen.fishinglogger.ui.WatchSyncViewModel
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val watchSyncViewModel: WatchSyncViewModel =
                viewModel(factory = WatchSyncViewModel.Factory)

            val status by watchSyncViewModel.status.collectAsStateWithLifecycle()

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                watchSyncViewModel.events.collect { message ->
                    snackbarHostState.showSnackbar(message)
                }
            }

            FishingLoggerTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
                                val catches by vm.catches.collectAsStateWithLifecycle(initialValue = emptyList())
                                CatchListScreen(
                                    catches = catches,
                                    onCatchClick = { id ->
                                        navController.navigate(
                                            CatchDetail(id),
                                        )
                                    },
                                )
                            }

                            composable<CatchDetail> {
                                val vm: CatchDetailViewModel =
                                    viewModel(factory = CatchDetailViewModel.Factory)
                                val uiState by vm.uiState.collectAsStateWithLifecycle(
                                    initialValue = CatchDetailUiState(null, null),
                                )
                                CatchDetailScreen(uiState = uiState)
                            }

                            composable<LureCatalog> {
                                val vm: LureCatalogViewModel =
                                    viewModel(factory = LureCatalogViewModel.Factory)
                                val catalog by vm.catalog.collectAsStateWithLifecycle(
                                    initialValue = emptyList(),
                                )
                                LureCatalogScreen(catalog = catalog)
                            }
                        }
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

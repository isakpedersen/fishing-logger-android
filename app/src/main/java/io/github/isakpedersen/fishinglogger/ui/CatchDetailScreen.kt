package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.Lure
import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatchDetailScreen(
    uiState: CatchDetailUiState?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fangst") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake",
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp),
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        if (uiState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                // loading catch
            }
        } else if (uiState.catch == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Ingen fangst funnet.")
            }
        } else {
            val catch = uiState.catch
            val lure = uiState.lure
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                Text("Tidspunkt: ${formatTimestamp(catch.timestamp)}")
                Text("Art: ${formatSpecies(catch.species)}")
                Text("Vekt: ${formatWeight(catch.weight)}")
                Text("Agn: ${if (lure == null) "–" else formatLure(lure.model, lure.variant)}")
                Text("Notater: ${formatNotes(catch.notes)}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatchDetailScreenPreview() {
    FishingLoggerTheme {
        CatchDetailScreen(
            uiState = CatchDetailUiState(
                Catch(
                    timestamp = 1784552603,
                    species = "Ørret",
                    weight = 1250,
                    lat = null,
                    lon = null,
                    lureVariantId = 1,
                    rig = null,
                    notes = null,
                ),
                Lure(
                    variant = LureVariant(
                        id = 1,
                        lureModelId = 1,
                        color = "C/R",
                        weight = 10.0,
                        length = null,
                    ),
                    model = LureModel(
                        id = 1,
                        type = LureType.SLUK,
                        name = "Møresilda",
                        brand = "Remen",
                    ),
                ),
            ),
            onBack = { },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyCatchDetailScreenPreview() {
    FishingLoggerTheme {
        CatchDetailScreen(
            uiState = CatchDetailUiState(null, null),
            onBack = { },
        )
    }
}

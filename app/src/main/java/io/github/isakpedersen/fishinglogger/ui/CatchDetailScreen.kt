package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.Lure
import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@Composable
fun CatchDetailScreen(uiState: CatchDetailUiState, modifier: Modifier = Modifier) {
    val catch = uiState.catch

    if (catch == null) {
        Text(
            text = "Laster... / Ingen fangst funnet.",
            modifier = modifier,
        )
        return
    }

    val lure = uiState.lure

    Column(
        modifier = modifier,
    ) {
        Text("Tidspunkt: ${formatTimestamp(catch.timestamp)}")
        Text("Art: ${formatSpecies(catch.species)}")
        Text("Vekt: ${formatWeight(catch.weight)}")
        Text("Agn: ${if (lure == null) "–" else formatLure(lure.model, lure.variant)}")
        Text("Notater: ${formatNotes(catch.notes)}")
    }
}

@Preview(showBackground = true)
@Composable
fun CatchDetailScreenPreview() {
    FishingLoggerTheme {
        CatchDetailScreen(
            CatchDetailUiState(
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
        )
    }
}

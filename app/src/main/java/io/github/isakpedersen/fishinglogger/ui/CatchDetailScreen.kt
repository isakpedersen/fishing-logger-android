package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@Composable
fun CatchDetailScreen(catch: Catch?, modifier: Modifier = Modifier) {
    if (catch == null) {
        Text(
            text = "Laster... / Ingen fangst funnet.",
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier,
    ) {
        Text("Tidspunkt: ${formatTimestamp(catch.timestamp)}")
        Text("Art: ${formatSpecies(catch.species)}")
        Text("Vekt: ${formatWeight(catch.weight)}")
        Text("Notater: ${formatNotes(catch.notes)}")
    }
}

@Preview(showBackground = true)
@Composable
fun CatchDetailScreenPreview() {
    FishingLoggerTheme {
        CatchDetailScreen(
            Catch(
                timestamp = 1784552603,
                species = "Ørret",
                weight = 1250,
                lat = null,
                lon = null,
                lureVariantId = null,
                rig = null,
                notes = null,
            ),
        )
    }
}

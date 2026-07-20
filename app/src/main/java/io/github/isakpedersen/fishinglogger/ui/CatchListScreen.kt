package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@Composable
fun CatchListScreen(
    catches: List<Catch>,
    onCatchClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = catches,
            key = { catch -> catch.timestamp },
        ) { catch ->
            CatchRow(catch, onCatchClick, Modifier.fillParentMaxWidth())
        }
    }
}

@Composable
private fun CatchRow(catch: Catch, onCatchClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.clickable { onCatchClick(catch.id) },
    ) {
        Text(formatTimestamp(catch.timestamp))
        Text(formatSpecies(catch.species), modifier = Modifier.weight(1f))
        Text(formatNotes(catch.notes))
        Text(formatWeight(catch.weight))
    }
}

@Preview(showBackground = true)
@Composable
fun CatchListScreenPreview() {
    FishingLoggerTheme {
        CatchListScreen(
            catches = listOf(
                previewCatch(timestamp = 1784120800, species = "Ørret", weight = 1000),
                previewCatch(timestamp = 1784120900, species = "Røye", weight = 750),
                previewCatch(timestamp = 1784121000, species = "Laks", weight = 9000),
                previewCatch(timestamp = 1784121100, species = null, weight = null),
                previewCatch(timestamp = 0, species = null, weight = null),
            ),
            onCatchClick = { },
        )
    }
}

private fun previewCatch(timestamp: Long, species: String?, weight: Int?): Catch {
    return Catch(
        timestamp = timestamp, species = species, weight = weight, lat = null, lon = null,
        lureVariantId = null, rig = null, notes = null,
    )
}

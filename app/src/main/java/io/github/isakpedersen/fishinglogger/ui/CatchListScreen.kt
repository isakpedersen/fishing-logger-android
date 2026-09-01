package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        if (catches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Ingen fangster")
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
            ) {
                items(
                    items = catches,
                    key = { catch -> catch.timestamp },
                ) { catch ->
                    CatchRow(catch, onCatchClick, Modifier.fillParentMaxWidth())
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun CatchRow(catch: Catch, onCatchClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(formatSpecies(catch.species)) },
        overlineContent = { Text(formatTimestamp(catch.timestamp)) },
        supportingContent = if (catch.notes.isNullOrBlank()) null else {
            { Text(formatNotes(catch.notes), maxLines = 1, overflow = TextOverflow.Ellipsis) }
        },
        trailingContent = {
            Text(
                formatWeight(catch.weight),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        modifier = modifier.clickable { onCatchClick(catch.id) },
    )
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
                previewCatch(
                    timestamp = 1788277116, species = "Ørret", weight = 1200,
                    notes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                ),
            ),
            onCatchClick = { },
        )
    }
}

private fun previewCatch(
    timestamp: Long,
    species: String?,
    weight: Int?,
    notes: String? = null,
): Catch {
    return Catch(
        timestamp = timestamp, species = species, weight = weight, lat = null, lon = null,
        lureVariantId = null, rig = null, notes = notes,
    )
}

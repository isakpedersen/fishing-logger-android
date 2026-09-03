package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.ui.components.DeleteDialog
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatchListScreen(
    catches: List<Catch>?,
    onCatchClick: (Long) -> Unit,
    onSaveCatch: (Catch) -> Unit,
    onDeleteCatch: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var addCatchTimestamp by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteCatchId by rememberSaveable { mutableStateOf<Long?>(null) }

    val grouped = remember(catches) { catches.orEmpty().groupBy { localDateOf(it.timestamp) } }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fangster") },
                windowInsets = WindowInsets(0.dp),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addCatchTimestamp = Instant.now().epochSecond
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Legg til fangst",
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        if (catches == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                // loading catches
            }
        } else if (catches.isEmpty()) {
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
                grouped.forEach { (date, catchesOnDate) ->
                    stickyHeader(
                        key = "header-$date",
                    ) {
                        DateHeader(date, catchesOnDate.size)
                    }
                    items(
                        items = catchesOnDate,
                        key = { catch -> catch.timestamp },
                    ) { catch ->
                        CatchRow(
                            catch = catch,
                            onClick = {
                                onCatchClick(catch.id)
                            },
                            onLongClick = {
                                deleteCatchId = catch.id
                            },
                            modifier = Modifier.fillParentMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    addCatchTimestamp?.let { timestamp ->
        AddCatchDialog(
            timestamp = timestamp,
            onDismiss = { addCatchTimestamp = null },
            onSave = { catch ->
                onSaveCatch(catch)
                addCatchTimestamp = null
            },
        )
    }

    deleteCatchId?.let { catchId ->
        DeleteDialog(
            label = "fangst",
            onDismiss = {
                deleteCatchId = null
            },
            onDelete = {
                onDeleteCatch(catchId)
                deleteCatchId = null
            },
        )
    }
}

@Composable
private fun AddCatchDialog(
    timestamp: Long,
    onDismiss: () -> Unit,
    onSave: (Catch) -> Unit,
) {
    var speciesText by rememberSaveable { mutableStateOf("") }
    var weightText by rememberSaveable { mutableStateOf("") }
    var notesText by rememberSaveable { mutableStateOf("") }

    val species = speciesText.trim().takeIf { it.isNotBlank() }
    val weight = weightText.toIntOrNull()
    val notes = notesText.trim().takeIf { it.isNotBlank() }

    val weightOk = weightText.isBlank() || weight != null

    AlertDialog(
        title = {
            Text("Ny fangst")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = speciesText,
                    onValueChange = { speciesText = it },
                    label = { Text("Art") },
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter(Char::isDigit) },
                    label = { Text("Vekt") },
                    suffix = { Text("g") },
                    isError = !weightOk,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notat") },
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val catch = Catch(
                        timestamp = timestamp,
                        species = species,
                        weight = weight,
                        lat = null,
                        lon = null,
                        lureVariantId = null,
                        rig = null,
                        notes = notes,
                    )
                    onSave(catch)
                },
                enabled = weightOk,
            ) {
                Text("Lagre")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Avbryt")
            }
        },
    )
}

@Composable
private fun CatchRow(
    catch: Catch,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = { Text(formatSpecies(catch.species)) },
        supportingContent = if (catch.notes.isNullOrBlank()) null else {
            { Text(formatNotes(catch.notes), maxLines = 1, overflow = TextOverflow.Ellipsis) }
        },
        leadingContent = {
            Text(
                text = formatTime(catch.timestamp),
                style = MaterialTheme.typography.labelLarge,
            )
        },
        trailingContent = {
            Text(
                text = formatWeight(catch.weight),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    )
}

@Composable
private fun DateHeader(date: LocalDate, numberOfCatches: Int, modifier: Modifier = Modifier) {
    Text(
        text = "${formatDate(date)} (${formatCatchCount(numberOfCatches)})",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
            onSaveCatch = { },
            onDeleteCatch = { },
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

package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureModelWithVariants
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@Composable
fun LureCatalogScreen(
    catalog: List<LureModelWithVariants>,
    expandedModelIds: Set<Long>,
    onModelClick: (Long) -> Unit,
    onSaveModel: (LureType, String, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Legg til modell",
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        if (catalog.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Katalogen er tom")
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
            ) {
                catalog.forEach { modelWithVariants ->
                    item(key = "model-${modelWithVariants.model.id}") {
                        LureModelRow(
                            model = modelWithVariants.model,
                            onClick = { onModelClick(modelWithVariants.model.id) },
                            modifier = Modifier.fillParentMaxWidth(),
                        )
                    }
                    if (modelWithVariants.model.id in expandedModelIds) {
                        items(
                            items = modelWithVariants.variants,
                            key = { variant -> "variant-${variant.id}" },
                        ) { variant ->
                            LureVariantRow(
                                variant,
                                Modifier
                                    .fillParentMaxWidth()
                                    .padding(start = 20.dp),
                            )
                        }
                        item(key = "add-variant-${modelWithVariants.model.id}") {
                            Row(modifier = Modifier.padding(start = 20.dp)) { Text("+ Legg til") }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddLureModelDialog(
            onDismiss = { showAddDialog = false },
            onSave = { type, name, brand ->
                onSaveModel(type, name, brand)
                showAddDialog = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLureModelDialog(
    onDismiss: () -> Unit,
    onSave: (LureType, String, String?) -> Unit,
) {
    var type: LureType by rememberSaveable { mutableStateOf(LureType.SLUK) }
    var name by rememberSaveable { mutableStateOf("") }
    var brand by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        title = {
            Text("Ny modell")
        },
        text = {
            Column {
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                ) {
                    OutlinedTextField(
                        value = type.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                    ) {
                        LureType.entries.forEach { lureType ->
                            DropdownMenuItem(
                                text = { Text(lureType.name) },
                                onClick = {
                                    type = lureType
                                    typeExpanded = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Navn") },
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Merke") },
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(type, name.trim(), brand.trim().ifBlank { null })
                },
                enabled = name.isNotBlank(),
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
private fun LureModelRow(model: LureModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Text("${model.type} ${model.name}")
    }
}

@Composable
private fun LureVariantRow(variant: LureVariant, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
    ) {
        Text("${variant.weight} ${variant.color}")
    }
}

@Preview(showBackground = true)
@Composable
fun LureCatalogScreenPreview() {
    FishingLoggerTheme {
        LureCatalogScreen(
            catalog = listOf(
                LureModelWithVariants(
                    LureModel(
                        id = 1,
                        type = LureType.SLUK,
                        name = "Møresilda",
                        brand = "Remen",
                    ),
                    listOf(
                        LureVariant(
                            id = 1,
                            lureModelId = 1,
                            color = "C/R",
                            weight = 10.0,
                            length = null,
                        ),
                        LureVariant(
                            id = 2,
                            lureModelId = 1,
                            color = "C/R",
                            weight = 15.0,
                            length = null,
                        ),
                    ),
                ),
            ),
            expandedModelIds = setOf(1),
            onModelClick = {},
            onSaveModel = { _, _, _ -> },
        )
    }
}

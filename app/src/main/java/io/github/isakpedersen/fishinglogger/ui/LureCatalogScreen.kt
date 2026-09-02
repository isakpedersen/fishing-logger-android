package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureModelWithVariants
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import io.github.isakpedersen.fishinglogger.ui.theme.FishingLoggerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LureCatalogScreen(
    catalog: List<LureModelWithVariants>,
    expandedModelIds: Set<Long>,
    onModelClick: (Long) -> Unit,
    onSaveModel: (LureType, String, String?) -> Unit,
    onSaveVariant: (Long, String?, Double?, Double?) -> Unit,
    onDeleteModel: (Long, String) -> Unit,
    onDeleteVariant: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var addVariantForModelId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteModelId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteModelName by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteVariantId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteVariantName by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Slukkatalog") },
                windowInsets = WindowInsets(0.dp),
            )
        },
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
                            onLongClick = {
                                deleteModelId = modelWithVariants.model.id
                                deleteModelName = formatLureModel(modelWithVariants.model)
                            },
                            modifier = Modifier.fillParentMaxWidth(),
                        )
                    }
                    if (modelWithVariants.model.id in expandedModelIds) {
                        items(
                            items = modelWithVariants.variants,
                            key = { variant -> "variant-${variant.id}" },
                        ) { variant ->
                            LureVariantRow(
                                variant = variant,
                                onLongClick = {
                                    deleteVariantId = variant.id
                                    deleteVariantName = formatLure(modelWithVariants.model, variant)
                                },
                                modifier = Modifier
                                    .fillParentMaxWidth(),
                            )
                        }
                        item(key = "add-variant-${modelWithVariants.model.id}") {
                            Row(
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            addVariantForModelId = modelWithVariants.model.id
                                        },
                                    )
                                    .heightIn(48.dp)
                                    .fillParentMaxWidth()
                                    .padding(start = 32.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) { Text("+ Legg til") }
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

    addVariantForModelId?.let { modelId ->
        AddLureVariantDialog(
            onDismiss = { addVariantForModelId = null },
            onSave = { color, weight, length ->
                onSaveVariant(modelId, color, weight, length)
                addVariantForModelId = null
            },
        )
    }

    val modelId = deleteModelId
    val modelName = deleteModelName
    if (modelId != null && modelName != null) {
        DeleteLureDialog(
            label = modelName,
            onDismiss = {
                deleteModelId = null
                deleteModelName = null
            },
            onDelete = {
                onDeleteModel(modelId, modelName)
                deleteModelId = null
                deleteModelName = null
            },
        )
    }

    val variantId = deleteVariantId
    val variantName = deleteVariantName
    if (variantId != null && variantName != null) {
        DeleteLureDialog(
            label = variantName,
            onDismiss = {
                deleteVariantId = null
                deleteVariantName = null
            },
            onDelete = {
                onDeleteVariant(variantId, variantName)
                deleteVariantId = null
                deleteVariantName = null
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
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Merke") },
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Navn") },
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
private fun AddLureVariantDialog(
    onDismiss: () -> Unit,
    onSave: (String?, Double?, Double?) -> Unit,
) {
    var color: String by rememberSaveable { mutableStateOf("") }
    var weightText: String by rememberSaveable { mutableStateOf("") }
    var lengthText: String by rememberSaveable { mutableStateOf("") }

    val weight = weightText.trim().replace(",", ".").toDoubleOrNull()
    val length = lengthText.trim().replace(",", ".").toDoubleOrNull()

    val weightOk = weightText.isBlank() || weight != null
    val lengthOk = lengthText.isBlank() || length != null

    AlertDialog(
        title = {
            Text("Ny variant")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Farge") },
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch in ",." } },
                    label = { Text("Vekt") },
                    suffix = { Text("g") },
                    isError = !weightOk,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(
                    value = lengthText,
                    onValueChange = { lengthText = it.filter { ch -> ch.isDigit() || ch in ",." } },
                    label = { Text("Lengde") },
                    suffix = { Text("cm") },
                    isError = !lengthOk,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        color.trim().ifBlank { null },
                        weight,
                        length,
                    )
                },
                enabled = weightOk && lengthOk && (color.isNotBlank() || weight != null || length != null),
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
private fun DeleteLureDialog(
    label: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        text = {
            Text("Vil du slette $label?")
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDelete) { Text("Slett") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Avbryt") }
        },
    )
}

@Composable
private fun LureModelRow(
    model: LureModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .heightIn(48.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(formatLureModel(model))
    }
}

@Composable
private fun LureVariantRow(
    variant: LureVariant,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
            .heightIn(48.dp)
            .padding(start = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(formatLureVariant(variant))
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
            onSaveVariant = { _, _, _, _ -> },
            onDeleteModel = { _, _ -> },
            onDeleteVariant = { _, _ -> },
        )
    }
}

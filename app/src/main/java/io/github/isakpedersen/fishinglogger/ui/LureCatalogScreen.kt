package io.github.isakpedersen.fishinglogger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier,
) {
    if (catalog.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Katalogen er tom")
        }
    } else {
        LazyColumn(
            modifier = modifier,
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
                }
            }
        }
    }
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
        )
    }
}

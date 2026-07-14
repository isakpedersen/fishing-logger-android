package io.github.isakpedersen.fishinglogger.sync

import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant

sealed interface CatalogItem
data class Node(val label: String, val items: List<CatalogItem>) : CatalogItem
data class Leaf(val label: String, val id: Long) : CatalogItem

fun composeLureCatalog(models: List<LureModel>, variants: List<LureVariant>): List<CatalogItem> {
    val variantsByModel = variants.groupBy { it.lureModelId }
    val modelsWithVariants = models.filter { it.id in variantsByModel }
    val modelsByType = modelsWithVariants.groupBy { it.type }

    return modelsByType.map { (type, modelsOfType) ->
        val modelsByBrand = modelsOfType.groupBy { it.brand }
        Node(
            label = typeLabel(type),
            items = modelsByBrand.map { (brand, modelsOfBrand) ->
                val brandLabel = brand ?: "Uten merke"
                Node(
                    label = brandLabel,
                    items = modelsOfBrand.map { modelSubtree(it, variantsByModel.getValue(it.id)) },
                )
            },
        )
    }
}

fun CatalogItem.toWire(): Map<String, Any> = when (this) {
    is Leaf -> mapOf("label" to label, "id" to Math.toIntExact(id))
    is Node -> mapOf("label" to label, "items" to items.map { it.toWire() })
}

private fun typeLabel(type: LureType): String = when (type) {
    LureType.SLUK -> "Sluk"
    LureType.SPINNER -> "Spinner"
    LureType.WOBBLER -> "Wobbler"
    LureType.FLUE -> "Flue"
    LureType.MARK -> "Mark"
}

private fun modelSubtree(model: LureModel, variants: List<LureVariant>): CatalogItem {
    val variantsByColor = variants.groupBy { it.color }
    return Node(
        label = model.name,
        items = variantsByColor.map { (color, variantsOfColor) ->
            val colorLabel = color ?: "Ukjent farge"
            val leaves = variantsOfColor.map {
                Leaf(
                    label = leafLabel(it),
                    id = it.id,
                )
            }
            Node(
                label = colorLabel,
                items = leaves,
            )
        },
    )
}

private fun leafLabel(variant: LureVariant): String = when {
    variant.weight != null -> measurementLabel(variant.weight, "g")
    variant.length != null -> measurementLabel(variant.length, "cm")
    else -> "Ukjent vekt/lengde"
}

private fun measurementLabel(value: Double, unit: String): String {
    val number = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
    return "$number $unit"
}

package io.github.isakpedersen.fishinglogger.ui

import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureVariant
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatTimestamp(epochSeconds: Long, zoneId: ZoneId = ZoneId.systemDefault()): String =
    Instant.ofEpochSecond(epochSeconds).atZone(zoneId).format(dateFormatter)

fun formatSpecies(species: String?): String = species ?: "–"

fun formatWeight(grams: Int?): String {
    if (grams == null) return "–"

    val kilos = grams / 1000.0
    return "%.2f kg".format(norwegianLocale, kilos)
}

fun formatNotes(notes: String?): String = notes ?: ""

fun formatLure(model: LureModel, variant: LureVariant): String {
    val type = model.type.name.lowercase().replaceFirstChar { it.uppercaseChar() }
    val weight = variant.weight?.let { formatMeasurement(it, "g") }
    val length = variant.length?.let { formatMeasurement(it, "cm") }
    val archived = if (variant.archived) "(arkivert)" else null

    return listOfNotNull(
        "($type)",
        model.brand,
        model.name,
        variant.color,
        length,
        weight,
        archived,
    ).joinToString(" ")
}

fun formatMeasurement(value: Double, unit: String): String =
    "${measurementFormat.format(value)} $unit"

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
private val norwegianLocale = Locale.forLanguageTag("nb-NO")

private val measurementFormat = NumberFormat.getNumberInstance(norwegianLocale).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 1
    roundingMode = RoundingMode.HALF_UP
}

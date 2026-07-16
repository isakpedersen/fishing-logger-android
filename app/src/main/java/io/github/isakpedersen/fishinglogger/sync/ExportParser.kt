package io.github.isakpedersen.fishinglogger.sync

import android.util.Log
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.Rig

fun parseEntries(entries: List<*>, knownVariantIds: Set<Long>): List<Catch> =
    entries.mapNotNull { parseEntry(it, knownVariantIds) }

private fun parseEntry(entry: Any?, knownVariantIds: Set<Long>): Catch? {
    if (entry !is Map<*, *>) {
        Log.w("ExportParser", "entry skipped: not a map (was ${entry?.javaClass?.simpleName})")
        return null
    }
    val timestamp = (entry["timestamp"] as? Number)?.toLong()
    if (timestamp == null) {
        Log.w(
            "ExportParser",
            "entry skipped: timestamp missing or not a number (was ${entry["timestamp"]?.javaClass?.simpleName})",
        )
        return null
    }

    val (lat, lon) = parseCoords(entry["coords"])
    val resolvedLure = resolveLure(entry["lure_variant_id"], knownVariantIds)
    return Catch(
        timestamp = timestamp,
        species = entry["species"] as? String,
        weight = (entry["fish_weight"] as? Number)?.toInt(),
        lat = lat,
        lon = lon,
        lureVariantId = resolvedLure.variantId,
        rig = parseRig(entry["rig"]),
        notes = resolvedLure.notes,
    )
}

private fun parseCoords(value: Any?): Pair<Double?, Double?> {
    if (value == null) {
        return Pair(null, null)
    }
    if (value !is List<*>) {
        Log.w("ExportParser", "coords dropped: not a list (was $value)")
        return Pair(null, null)
    }

    val lat = value.getOrNull(0) as? Number
    val lon = value.getOrNull(1) as? Number

    if (lat == null || lon == null) {
        Log.w("ExportParser", "coords dropped: expected [lat, lon] numbers (was $value)")
        return Pair(null, null)
    }

    return Pair(lat.toDouble(), lon.toDouble())
}

private fun resolveLure(value: Any?, knownVariantIds: Set<Long>): ResolvedLure = when (value) {
    null -> {
        ResolvedLure(null, null)
    }

    is Number -> {
        val id = value.toLong()
        val integral = value.toDouble() == id.toDouble()
        when {
            !integral -> {
                Log.w("ExportParser", "lure_variant_id not integral: $value (kept in notes)")
                ResolvedLure(null, "ugyldig sluk-id: $value")
            }

            id in knownVariantIds -> {
                ResolvedLure(id, null)
            }

            else -> {
                Log.w("ExportParser", "lure_variant_id unknown: $id (kept in notes)")
                ResolvedLure(null, "ukjent sluk-id: $id")
            }
        }
    }

    else -> {
        Log.w("ExportParser", "lure_variant_id not a number: $value (kept in notes)")
        ResolvedLure(null, "ugyldig sluk-id: $value")
    }
}

private data class ResolvedLure(val variantId: Long?, val notes: String?)

private fun parseRig(value: Any?): Rig? {
    if (value == null) return null

    return when (value) {
        "Fastdupp" -> Rig.FASTDUPP
        "Glidedupp" -> Rig.GLIDEDUPP
        "Bunnmeite" -> Rig.BUNNMEITE
        else -> {
            Log.w("ExportParser", "rig dropped: unknown value (was $value)")
            null
        }
    }
}

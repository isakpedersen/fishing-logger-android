package io.github.isakpedersen.fishinglogger.sync

import android.util.Log
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.Rig

fun parseEntries(entries: List<*>): List<Catch> =
    entries.mapNotNull { parseEntry(it) }

private fun parseEntry(entry: Any?): Catch? {
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
    return Catch(
        timestamp = timestamp,
        species = entry["species"] as? String,
        weight = (entry["fish_weight"] as? Number)?.toInt(),
        lat = lat,
        lon = lon,
        lureVariantId = (entry["lure_variant_id"] as? Number)?.toLong(),
        rig = parseRig(entry["rig"]),
        notes = null,
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

package io.github.isakpedersen.fishinglogger.sync

import android.util.Log
import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.Rig

private const val TAG = "ExportParser"

fun parseEntries(entries: List<*>, knownVariantIds: Set<Long>): List<Catch> =
    entries.mapNotNull { parseEntry(it, knownVariantIds) }

private fun parseEntry(entry: Any?, knownVariantIds: Set<Long>): Catch? {
    if (entry !is Map<*, *>) {
        Log.w(TAG, "entry skipped: not a map (was ${entry?.javaClass?.simpleName})")
        return null
    }
    val timestamp = (entry["timestamp"] as? Number)?.toLong()
    if (timestamp == null) {
        Log.w(
            TAG,
            "entry skipped: timestamp missing or not a number (was ${entry["timestamp"]?.javaClass?.simpleName})",
        )
        return null
    }

    val (lat, lon) = parseCoords(entry["coords"])
    val resolvedLure = resolveLure(entry["lure_variant_id"], knownVariantIds)
    val resolvedRig = resolveRig(entry["rig"])
    val notes = listOfNotNull(resolvedLure.note, resolvedRig.note)
        .joinToString(", ")
        .ifEmpty { null }
    return Catch(
        timestamp = timestamp,
        species = entry["species"] as? String,
        weight = (entry["fish_weight"] as? Number)?.toInt(),
        lat = lat,
        lon = lon,
        lureVariantId = resolvedLure.variantId,
        rig = resolvedRig.rig,
        notes = notes,
    )
}

private fun parseCoords(value: Any?): Pair<Double?, Double?> {
    if (value == null) {
        return Pair(null, null)
    }
    if (value !is List<*>) {
        Log.w(TAG, "coords dropped: not a list (was $value)")
        return Pair(null, null)
    }

    val lat = value.getOrNull(0) as? Number
    val lon = value.getOrNull(1) as? Number

    if (lat == null || lon == null) {
        Log.w(TAG, "coords dropped: expected [lat, lon] numbers (was $value)")
        return Pair(null, null)
    }

    return Pair(lat.toDouble(), lon.toDouble())
}

private fun resolveLure(value: Any?, knownVariantIds: Set<Long>): ResolvedLure = when (value) {
    null -> ResolvedLure(null)

    is Number -> {
        val id = value.toLong()
        val integral = value.toDouble() == id.toDouble()
        when {
            !integral -> {
                Log.w(TAG, "lure_variant_id not integral: $value (kept in notes)")
                ResolvedLure(null, "ugyldig sluk-id: $value")
            }

            id in knownVariantIds -> ResolvedLure(id)

            else -> {
                Log.w(TAG, "lure_variant_id unknown: $id (kept in notes)")
                ResolvedLure(null, "ukjent sluk-id: $id")
            }
        }
    }

    else -> {
        Log.w(TAG, "lure_variant_id not a number: $value (kept in notes)")
        ResolvedLure(null, "ugyldig sluk-id: $value")
    }
}

private data class ResolvedLure(val variantId: Long?, val note: String? = null)

private fun resolveRig(value: Any?): ResolvedRig {
    val rig = Rig.entries.firstOrNull { it.wireName == value }
    return when {
        value == null -> ResolvedRig(null)
        rig != null -> ResolvedRig(rig)
        else -> {
            Log.w(TAG, "rig unknown: $value (kept in notes)")
            ResolvedRig(null, "ukjent rigg: $value")
        }
    }
}

private data class ResolvedRig(val rig: Rig?, val note: String? = null)

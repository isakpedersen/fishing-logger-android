package io.github.isakpedersen.fishinglogger.sync

import io.github.isakpedersen.fishinglogger.data.Catch
import io.github.isakpedersen.fishinglogger.data.Rig
import org.junit.Assert.assertEquals
import org.junit.Test

class ExportParserTest {
    @Test
    fun `full entry parses all fields`() {
        val entry = mapOf(
            "timestamp" to 1784067024,
            "coords" to listOf(59.911491, 10.757933),
            "species" to "Ørret",
            "fish_weight" to 1000,
            "lure_variant_id" to 35,
            "rig" to "Bunnmeite",
        )

        val expected = listOf(
            Catch(
                timestamp = 1784067024,
                species = "Ørret",
                weight = 1000,
                lat = 59.911491,
                lon = 10.757933,
                lureVariantId = 35,
                rig = Rig.BUNNMEITE,
                notes = null,
            ),
        )

        val result = parseEntries(listOf(entry), setOf(35L))

        assertEquals(expected, result)
    }

    @Test
    fun `timestamp-only entry parses all optional fields as null`() {
        val entry = mapOf(
            "timestamp" to 1784067024,
        )

        val expected = listOf(
            nullCatch(1784067024),
        )

        val result = parseEntries(listOf(entry), setOf())

        assertEquals(expected, result)
    }

    @Test
    fun `entry with missing timestamp is skipped, while others are parsed`() {
        val entries = listOf(
            mapOf(
                "species" to "Ørret",
            ),
            mapOf(
                "timestamp" to 1784067024,
                "species" to "Røye",
            ),
        )

        val expected = listOf(
            Catch(
                timestamp = 1784067024,
                species = "Røye",
                weight = null,
                lat = null,
                lon = null,
                lureVariantId = null,
                rig = null,
                notes = null,
            ),
        )

        val result = parseEntries(entries, setOf())

        assertEquals(expected, result)
    }

    @Test
    fun `non-map entries are skipped, while others are parsed`() {
        val entries = listOf(
            "This is not a map",
            listOf(
                "This is not inside a map",
            ),
            mapOf(
                "timestamp" to 1784067024,
            ),
        )

        val expected = listOf(
            nullCatch(1784067024),
        )

        val result = parseEntries(entries, setOf())

        assertEquals(expected, result)
    }

    @Test
    fun `malformed coords degrade to null`() {
        val entries = listOf(
            mapOf(
                "timestamp" to 1784067024,
                "coords" to listOf(59.9),
            ),
            mapOf(
                "timestamp" to 1784070058,
                "coords" to "59.9,10.7",
            ),
            mapOf(
                "timestamp" to 1784070308,
                "coords" to listOf(null, 10.7),
            ),
        )

        val expected = listOf(
            nullCatch(1784067024),
            nullCatch(1784070058),
            nullCatch(1784070308),
        )

        val result = parseEntries(entries, setOf())

        assertEquals(expected, result)
    }

    @Test
    fun `an unknown lure_variant_id degrades to a note`() {
        val entry = mapOf(
            "timestamp" to 1784159046,
            "lure_variant_id" to 2,
        )

        val expected = listOf(
            nullCatch(timestamp = 1784159046, notes = "ukjent sluk-id: 2"),
        )

        val result = parseEntries(listOf(entry), setOf(1L, 3L))

        assertEquals(expected, result)
    }

    @Test
    fun `a non-number lure_variant_id degrades to a note`() {
        val entries = listOf(
            mapOf(
                "timestamp" to 1784159000,
                "lure_variant_id" to "Garbage id",
            ),
            mapOf(
                "timestamp" to 1784159100,
                "lure_variant_id" to listOf(4),
            ),
        )

        val expected = listOf(
            nullCatch(timestamp = 1784159000, notes = "ugyldig sluk-id: Garbage id"),
            nullCatch(timestamp = 1784159100, notes = "ugyldig sluk-id: [4]"),
        )

        val result = parseEntries(entries, setOf(1L, 3L))

        assertEquals(expected, result)
    }

    @Test
    fun `a non-integral lure_variant_id degrades to a note`() {
        val entry = mapOf(
            "timestamp" to 1784159100,
            "lure_variant_id" to 2.5,
        )

        val expected = listOf(
            nullCatch(timestamp = 1784159100, notes = "ugyldig sluk-id: 2.5"),
        )

        val result = parseEntries(listOf(entry), setOf(2L, 3L))

        assertEquals(expected, result)
    }

    @Test
    fun `an unknown rig degrades to a note`() {
        val entry = mapOf(
            "timestamp" to 1784067024,
            "rig" to "This rig doesn't exist",
        )

        val expected = listOf(
            nullCatch(timestamp = 1784067024, notes = "ukjent rigg: This rig doesn't exist"),
        )

        val result = parseEntries(listOf(entry), setOf())

        assertEquals(expected, result)
    }

    @Test
    fun `unknown lure and rig degrade to a combined note`() {
        val entry = mapOf(
            "timestamp" to 1784297439,
            "lure_variant_id" to 2,
            "rig" to "Garbage rig",
        )

        val expected = listOf(
            nullCatch(
                timestamp = 1784297439,
                notes = "ukjent sluk-id: 2, ukjent rigg: Garbage rig"
            ),
        )

        val result = parseEntries(listOf(entry), setOf(1L, 3L))

        assertEquals(expected, result)
    }
}

private fun nullCatch(timestamp: Long, notes: String? = null) = Catch(
    timestamp = timestamp,
    species = null,
    weight = null,
    lat = null,
    lon = null,
    lureVariantId = null,
    rig = null,
    notes = notes,
)

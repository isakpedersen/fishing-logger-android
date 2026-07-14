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

        val result = parseEntries(listOf(entry))

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

        val result = parseEntries(listOf(entry))

        assertEquals(expected, result)
    }

    @Test
    fun `unknown rig degrades to null`() {
        val entry = mapOf(
            "timestamp" to 1784067024,
            "rig" to "This rig doesn't exist",
        )

        val expected = listOf(
            nullCatch(1784067024),
        )

        val result = parseEntries(listOf(entry))

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

        val result = parseEntries(entries)

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

        val result = parseEntries(entries)

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

        val result = parseEntries(entries)

        assertEquals(expected, result)
    }
}

private fun nullCatch(timestamp: Long) = Catch(
    timestamp = timestamp,
    species = null,
    weight = null,
    lat = null,
    lon = null,
    lureVariantId = null,
    rig = null,
    notes = null,
)

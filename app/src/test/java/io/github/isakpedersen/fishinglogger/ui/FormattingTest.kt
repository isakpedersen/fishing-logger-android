package io.github.isakpedersen.fishinglogger.ui

import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

class FormattingTest {
    @Test
    fun `timestamp is shown as date and time in Norwegian time zone`() {
        assertEquals("15.07.2026 14:29", formatTimestamp(1784118584, ZoneId.of("Europe/Oslo")))
    }

    @Test
    fun `species is shown as-is, or en-dash if null`() {
        assertEquals("Ørret", formatSpecies("Ørret"))
        assertEquals("–", formatSpecies(null))
    }

    @Test
    fun `weight is shown in kg, using comma and two decimals, or en-dash if null`() {
        assertEquals("1,00 kg", formatWeight(1000))
        assertEquals("0,75 kg", formatWeight(750))
        assertEquals("1,25 kg", formatWeight(1250))
        assertEquals("–", formatWeight(null))
    }

    @Test
    fun `show all fields in order when lure is fully filled-in`() {
        val model = LureModel(
            id = 1,
            type = LureType.WOBBLER,
            name = "Countdown",
            brand = "Rapala",
        )

        val variant = LureVariant(
            id = 1,
            lureModelId = 1,
            color = "RT",
            weight = 10.5,
            length = 9.0,
            archived = true,
        )

        val expected = "(Wobbler) Rapala Countdown RT 9 cm 10,5 g (arkivert)"

        val result = formatLure(model, variant)

        assertEquals(expected, result)
    }

    @Test
    fun `omits null fields without leaving extra spaces`() {
        val model = LureModel(
            id = 1,
            type = LureType.SLUK,
            name = "Møresilda",
            brand = null,
        )

        val variant = LureVariant(
            id = 1,
            lureModelId = 1,
            color = null,
            weight = 5.0,
            length = null,
            archived = false,
        )

        val expected = "(Sluk) Møresilda 5 g"

        val result = formatLure(model, variant)

        assertEquals(expected, result)
    }

    @Test
    fun `formats a whole number with no decimals`() {
        assertEquals("7 g", formatMeasurement(7.0, "g"))
    }

    @Test
    fun `formats a decimal with a comma separator`() {
        assertEquals("7,5 g", formatMeasurement(7.5, "g"))
    }

    @Test
    fun `rounds to one decimal place`() {
        assertEquals("7,3 g", formatMeasurement(7.25, "g"))
    }
}

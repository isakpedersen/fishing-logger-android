package io.github.isakpedersen.fishinglogger.ui

import io.github.isakpedersen.fishinglogger.data.LureModel
import io.github.isakpedersen.fishinglogger.data.LureType
import io.github.isakpedersen.fishinglogger.data.LureVariant
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class FormattingTest {
    @Test
    fun `timestamp is shown as date and time in Norwegian time zone`() {
        assertEquals("15.07.2026 14:29", formatTimestamp(1784118584, ZoneId.of("Europe/Oslo")))
    }

    @Test
    fun `time is shown as time in Norwegian time zone`() {
        assertEquals("12:49", formatTime(1788346158, ZoneId.of("Europe/Oslo")))
    }

    @Test
    fun `date is shown as date in Norwegian format`() {
        assertEquals("31. august 2026", formatDate(date = LocalDate.of(2026, 8, 31), today = LocalDate.of(2026, 9, 2)))
    }

    @Test
    fun `today and yesterday are shown as relative labels`() {
        assertEquals("I dag", formatDate(date = LocalDate.of(2026, 9, 2), today = LocalDate.of(2026, 9, 2)))
        assertEquals("I går", formatDate(date = LocalDate.of(2026, 9, 1), today = LocalDate.of(2026, 9, 2)))
        assertEquals("I går", formatDate(date = LocalDate.of(2026, 8, 31), today = LocalDate.of(2026, 9, 1)))
        assertEquals("I går", formatDate(date = LocalDate.of(2025, 12, 31), today = LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun `localDateOf finds the right date around midnight`() {
        assertEquals(LocalDate.of(2026, 9, 1), localDateOf(1788299999, ZoneId.of("Europe/Oslo")))
        assertEquals(LocalDate.of(2026, 9, 2), localDateOf(1788300000, ZoneId.of("Europe/Oslo")))
        assertEquals(LocalDate.of(2026, 9, 2), localDateOf(1788300001, ZoneId.of("Europe/Oslo")))
    }

    @Test
    fun `localDateOf handles different timezones`() {
        assertEquals(LocalDate.of(2026, 9, 1), localDateOf(1788303600, ZoneId.of("UTC")))
        assertEquals(LocalDate.of(2026, 9, 2), localDateOf(1788303600, ZoneId.of("Europe/Oslo")))
    }

    @Test
    fun `catch count handles singular and plural grammar`() {
        assertEquals("0 fangster", formatCatchCount(0))
        assertEquals("1 fangst", formatCatchCount(1))
        assertEquals("2 fangster", formatCatchCount(2))
        assertEquals("100 fangster", formatCatchCount(100))
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
    fun `only color set in variant`() {
        val variant = LureVariant(
            lureModelId = 1,
            color = "C/R",
            weight = null,
            length = null,
        )

        val expected = "C/R"

        val result = formatLureVariant(variant)

        assertEquals(expected, result)
    }

    @Test
    fun `all fields null in variant leaves no trailing space`() {
        val model = LureModel(
            id = 1,
            type = LureType.SLUK,
            name = "Møresilda",
            brand = null,
        )

        val variant = LureVariant(
            lureModelId = 1,
            color = null,
            weight = null,
            length = null,
        )

        val expected = "(Sluk) Møresilda"

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

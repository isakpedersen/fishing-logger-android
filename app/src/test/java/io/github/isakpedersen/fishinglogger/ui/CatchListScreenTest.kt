package io.github.isakpedersen.fishinglogger.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

class CatchListScreenTest {
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
}

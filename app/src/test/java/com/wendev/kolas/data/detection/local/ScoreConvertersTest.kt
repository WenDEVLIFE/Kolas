package com.wendev.kolas.data.detection.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreConvertersTest {

    private val converters = ScoreConverters()

    @Test
    fun `round-trips a score map`() {
        val scores = mapOf("alert" to 0.1f, "angry" to 0.2f, "happy" to 0.7f)
        assertEquals(scores, converters.toScores(converters.fromScores(scores)))
    }

    @Test
    fun `empty map round-trips`() {
        val empty = emptyMap<String, Float>()
        assertEquals(empty, converters.toScores(converters.fromScores(empty)))
    }

    @Test
    fun `malformed json decodes to empty map`() {
        assertEquals(emptyMap<String, Float>(), converters.toScores("not-json"))
    }
}

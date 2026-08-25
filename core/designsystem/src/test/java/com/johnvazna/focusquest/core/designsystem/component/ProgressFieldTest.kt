package com.johnvazna.focusquest.core.designsystem.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.ceil

class ProgressFieldTest {
    @Test
    fun `the empty field paints nothing in`() {
        assertTrue(progressFieldDots(progress = 0f).none { it.lit })
    }

    @Test
    fun `every ring is evenly divided`() {
        val dots = progressFieldDots(progress = 0f)
        (1..PROGRESS_FIELD_RINGS).forEach { ring ->
            val ringDots = dots.filter { it.ring == ring }
            val step = 360f / ringDots.size
            ringDots.forEachIndexed { index, dot ->
                assertEquals(-90f + step * index, dot.angleDegrees, TOLERANCE)
            }
        }
    }

    @Test
    fun `outer rings hold more dots than inner rings`() {
        val dots = progressFieldDots(progress = 0f)
        val counts = (1..PROGRESS_FIELD_RINGS).map { ring -> dots.count { it.ring == ring } }
        assertEquals(counts.sorted(), counts)
        assertTrue(counts.first() < counts.last())
    }

    @Test
    fun `progress paints a single ring proportionally`() {
        val dots = progressFieldDots(progress = 0.5f)
        val lit = dots.filter { it.lit }
        val litRing = lit.map { it.ring }.distinct()
        val ringSize = dots.count { it.ring == litRing.single() }
        assertEquals(1, litRing.size)
        assertEquals(ceil(ringSize * 0.5f).toInt(), lit.size)
    }

    @Test
    fun `a full project paints its whole ring`() {
        val dots = progressFieldDots(progress = 1f)
        val lit = dots.filter { it.lit }
        assertEquals(dots.count { it.ring == lit.first().ring }, lit.size)
    }

    @Test
    fun `entrance is staggered from the centre outwards`() {
        val dots = progressFieldDots(progress = 0f)
        val firstRing = dots.filter { it.ring == 1 }.minOf { it.delaySeconds }
        val lastRing = dots.filter { it.ring == PROGRESS_FIELD_RINGS }.minOf { it.delaySeconds }
        assertTrue(firstRing < lastRing)
    }

    @Test
    fun `the entrance timeline outlasts the slowest dot`() {
        val dots = progressFieldDots(progress = 0f)
        val slowestStartMillis = dots.maxOf { it.delaySeconds } * 1000f
        assertTrue(
            progressFieldEntranceDurationMillis(dots) >=
                slowestStartMillis + PROGRESS_FIELD_ENTRANCE_DURATION_MS,
        )
    }

    @Test
    fun `progress outside zero to one is rejected`() {
        listOf(-0.01f, 1.01f).forEach { invalid ->
            assertFalse(runCatching { progressFieldDots(invalid) }.isSuccess)
        }
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}

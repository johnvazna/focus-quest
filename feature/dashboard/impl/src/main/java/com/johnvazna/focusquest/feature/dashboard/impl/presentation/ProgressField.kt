package com.johnvazna.focusquest.feature.dashboard.impl.presentation

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * One polar dot field.
 *
 * The progress ring is a ring *of* the field rather than a separate wheel: a single ring is
 * painted in as the project advances. With no project there is nothing to paint, so every dot
 * renders in its resting form.
 *
 * Geometry is kept free of Compose and of units so it stays directly testable.
 */
internal data class ProgressFieldDot(
    val ring: Int,
    val angleDegrees: Float,
    val lit: Boolean,
    val delaySeconds: Float,
    val holdsInk: Boolean,
)

internal const val PROGRESS_FIELD_RINGS = 9
internal const val PROGRESS_FIELD_DOT_DIAMETER_DP = 2.2f
internal const val PROGRESS_FIELD_LIT_DOT_DIAMETER_DP = 7f

private const val PROGRESS_RING = 4
private const val MIN_DOTS_PER_RING = 6

/**
 * Dot entrance is staggered by ring and by position within the ring. The longest-delayed dot
 * still finishes within [PROGRESS_FIELD_ENTRANCE_DURATION_MS] of its own start.
 */
internal const val PROGRESS_FIELD_ENTRANCE_DURATION_MS = 2200
private const val RING_DELAY_SECONDS = 0.14f
private const val SWEEP_DELAY_SECONDS = 0.34f

internal fun progressFieldDots(progress: Float): List<ProgressFieldDot> {
    require(progress in 0f..1f) { "Progress must be between zero and one." }

    val dots = mutableListOf<ProgressFieldDot>()
    for (ring in 1..PROGRESS_FIELD_RINGS) {
        val dotsInRing = max(MIN_DOTS_PER_RING, (2.0 * PI * ring).roundToInt())
        val isProgressRing = ring == PROGRESS_RING
        for (index in 0 until dotsInRing) {
            val sweep = index.toFloat() / dotsInRing
            dots += ProgressFieldDot(
                ring = ring,
                angleDegrees = -90f + 360f * sweep,
                lit = isProgressRing && sweep < progress,
                delaySeconds = ring * RING_DELAY_SECONDS + sweep * SWEEP_DELAY_SECONDS,
                holdsInk = (index * 7 + ring * 3) % 9 == 0,
            )
        }
    }
    return dots
}

internal fun progressFieldEntranceDurationMillis(dots: List<ProgressFieldDot>): Int =
    ceil(dots.maxOf { it.delaySeconds } * 1000f).toInt() + PROGRESS_FIELD_ENTRANCE_DURATION_MS

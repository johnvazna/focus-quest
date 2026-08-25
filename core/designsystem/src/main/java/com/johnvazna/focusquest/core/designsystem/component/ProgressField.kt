package com.johnvazna.focusquest.core.designsystem.component

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

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
internal const val PROGRESS_FIELD_ENTRANCE_DURATION_MS = 2200

private const val PROGRESS_RING = 4
private const val MIN_DOTS_PER_RING = 6
private const val RING_DELAY_SECONDS = 0.14f
private const val SWEEP_DELAY_SECONDS = 0.34f

internal fun progressFieldDots(progress: Float): List<ProgressFieldDot> {
    require(progress in 0f..1f) { "Progress must be between zero and one." }
    return buildList {
        for (ring in 1..PROGRESS_FIELD_RINGS) {
            val dotsInRing = max(MIN_DOTS_PER_RING, (2.0 * PI * ring).roundToInt())
            val isProgressRing = ring == PROGRESS_RING
            for (index in 0 until dotsInRing) {
                val sweep = index.toFloat() / dotsInRing
                add(
                    ProgressFieldDot(
                        ring = ring,
                        angleDegrees = -90f + 360f * sweep,
                        lit = isProgressRing && sweep < progress,
                        delaySeconds = ring * RING_DELAY_SECONDS + sweep * SWEEP_DELAY_SECONDS,
                        holdsInk = (index * 7 + ring * 3) % 9 == 0,
                    ),
                )
            }
        }
    }
}

internal fun progressFieldEntranceDurationMillis(dots: List<ProgressFieldDot>): Int =
    ceil(dots.maxOf { it.delaySeconds } * 1000f).toInt() + PROGRESS_FIELD_ENTRANCE_DURATION_MS

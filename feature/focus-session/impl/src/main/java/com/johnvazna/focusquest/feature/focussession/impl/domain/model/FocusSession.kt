package com.johnvazna.focusquest.feature.focussession.impl.domain.model

@ConsistentCopyVisibility
data class FocusSession private constructor(
    val plannedDurationMinutes: Int,
    val remainingDurationMinutes: Int,
    val pauseCount: Int,
    val status: FocusSessionStatus,
    val earnedExperiencePoints: Int,
) {

    fun pause(): FocusSession {
        check(status == FocusSessionStatus.ACTIVE) { "Only an active session can be paused." }
        check(pauseCount < FocusSessionPolicy.MAX_PAUSE_COUNT) { "The pause limit has been reached." }

        return copy(
            pauseCount = pauseCount + 1,
            status = FocusSessionStatus.PAUSED,
        )
    }

    fun resume(): FocusSession {
        check(status == FocusSessionStatus.PAUSED) { "Only a paused session can be resumed." }
        return copy(status = FocusSessionStatus.ACTIVE)
    }

    fun advance(elapsedMinutes: Int): FocusSession {
        require(elapsedMinutes > 0) { "Elapsed minutes must be positive." }
        check(status == FocusSessionStatus.ACTIVE) { "Only an active session can advance." }

        val updatedRemainingMinutes = (remainingDurationMinutes - elapsedMinutes).coerceAtLeast(0)
        if (updatedRemainingMinutes > 0) {
            return copy(remainingDurationMinutes = updatedRemainingMinutes)
        }

        return copy(
            remainingDurationMinutes = 0,
            status = FocusSessionStatus.COMPLETED,
            earnedExperiencePoints = FocusSessionPolicy.experienceFor(plannedDurationMinutes),
        )
    }

    fun cancel(): FocusSession {
        check(status == FocusSessionStatus.ACTIVE || status == FocusSessionStatus.PAUSED) {
            "Only an active or paused session can be cancelled."
        }
        return copy(
            status = FocusSessionStatus.CANCELLED,
            earnedExperiencePoints = 0,
        )
    }

    companion object {
        fun start(durationMinutes: Int): FocusSession {
            require(FocusSessionPolicy.isValidDuration(durationMinutes)) {
                "Duration must be between ${FocusSessionPolicy.MIN_DURATION_MINUTES} and " +
                    "${FocusSessionPolicy.MAX_DURATION_MINUTES} minutes."
            }
            return FocusSession(
                plannedDurationMinutes = durationMinutes,
                remainingDurationMinutes = durationMinutes,
                pauseCount = 0,
                status = FocusSessionStatus.ACTIVE,
                earnedExperiencePoints = 0,
            )
        }
    }
}

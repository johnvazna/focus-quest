package com.johnvazna.focusquest.feature.focussession.impl.domain.model

object FocusSessionPolicy {
    const val MIN_DURATION_MINUTES = 5
    const val MAX_DURATION_MINUTES = 120
    const val MAX_PAUSE_COUNT = 3

    fun isValidDuration(durationMinutes: Int): Boolean =
        durationMinutes in MIN_DURATION_MINUTES..MAX_DURATION_MINUTES

    fun experienceFor(completedMinutes: Int): Int = completedMinutes
}

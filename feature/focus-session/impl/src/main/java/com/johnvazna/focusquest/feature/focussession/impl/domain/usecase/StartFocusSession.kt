package com.johnvazna.focusquest.feature.focussession.impl.domain.usecase

import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSessionPolicy
import com.johnvazna.focusquest.feature.focussession.impl.domain.repository.FocusSessionRepository

class StartFocusSession(
    private val repository: FocusSessionRepository,
) {
    suspend operator fun invoke(durationMinutes: Int): StartFocusSessionResult {
        if (!FocusSessionPolicy.isValidDuration(durationMinutes)) {
            return StartFocusSessionResult.InvalidDuration
        }

        val session = FocusSession.start(durationMinutes)
        return if (repository.startIfNoneActive(session)) {
            StartFocusSessionResult.Started(session)
        } else {
            StartFocusSessionResult.SessionAlreadyActive
        }
    }
}

sealed interface StartFocusSessionResult {
    data class Started(val session: FocusSession) : StartFocusSessionResult
    data object InvalidDuration : StartFocusSessionResult
    data object SessionAlreadyActive : StartFocusSessionResult
}

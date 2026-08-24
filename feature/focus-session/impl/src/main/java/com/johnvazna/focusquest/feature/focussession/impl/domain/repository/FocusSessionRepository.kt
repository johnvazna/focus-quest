package com.johnvazna.focusquest.feature.focussession.impl.domain.repository

import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession

fun interface FocusSessionRepository {
    suspend fun startIfNoneActive(session: FocusSession): Boolean
}

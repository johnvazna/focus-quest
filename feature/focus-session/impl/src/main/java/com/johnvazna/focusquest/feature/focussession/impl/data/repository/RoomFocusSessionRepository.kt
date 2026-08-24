package com.johnvazna.focusquest.feature.focussession.impl.data.repository

import com.johnvazna.focusquest.feature.focussession.impl.data.local.FocusSessionDao
import com.johnvazna.focusquest.feature.focussession.impl.data.mapper.toEntity
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession
import com.johnvazna.focusquest.feature.focussession.impl.domain.repository.FocusSessionRepository

class RoomFocusSessionRepository(
    private val dao: FocusSessionDao,
) : FocusSessionRepository {
    override suspend fun startIfNoneActive(session: FocusSession): Boolean =
        dao.insertIfNoneActive(session.toEntity()) != INSERT_CONFLICT

    private companion object {
        const val INSERT_CONFLICT = -1L
    }
}

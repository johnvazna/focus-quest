package com.johnvazna.focusquest.feature.focussession.impl.data.mapper

import com.johnvazna.focusquest.feature.focussession.impl.data.local.FocusSessionEntity
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSessionStatus

internal fun FocusSession.toEntity() = FocusSessionEntity(
    plannedDurationMinutes = plannedDurationMinutes,
    remainingDurationMinutes = remainingDurationMinutes,
    pauseCount = pauseCount,
    status = status.name,
    earnedExperiencePoints = earnedExperiencePoints,
)

internal fun FocusSessionEntity.toDomain() = FocusSession.restore(
    plannedDurationMinutes = plannedDurationMinutes,
    remainingDurationMinutes = remainingDurationMinutes,
    pauseCount = pauseCount,
    status = FocusSessionStatus.valueOf(status),
    earnedExperiencePoints = earnedExperiencePoints,
)

package com.johnvazna.focusquest.feature.focussession.impl.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_focus_session")
data class FocusSessionEntity(
    @PrimaryKey val slotId: Int = ACTIVE_SESSION_SLOT_ID,
    val plannedDurationMinutes: Int,
    val remainingDurationMinutes: Int,
    val pauseCount: Int,
    val status: String,
    val earnedExperiencePoints: Int,
)

internal const val ACTIVE_SESSION_SLOT_ID = 1

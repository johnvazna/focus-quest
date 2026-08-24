package com.johnvazna.focusquest.feature.focussession.impl.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNoneActive(session: FocusSessionEntity): Long

    @Query("SELECT * FROM active_focus_session WHERE slotId = :slotId LIMIT 1")
    suspend fun getActiveSession(slotId: Int = ACTIVE_SESSION_SLOT_ID): FocusSessionEntity?

    @Query("DELETE FROM active_focus_session WHERE slotId = :slotId")
    suspend fun clearActiveSession(slotId: Int = ACTIVE_SESSION_SLOT_ID)
}

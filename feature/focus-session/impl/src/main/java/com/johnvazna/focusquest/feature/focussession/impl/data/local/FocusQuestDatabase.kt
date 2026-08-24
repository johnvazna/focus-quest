package com.johnvazna.focusquest.feature.focussession.impl.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FocusSessionEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class FocusQuestDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
}

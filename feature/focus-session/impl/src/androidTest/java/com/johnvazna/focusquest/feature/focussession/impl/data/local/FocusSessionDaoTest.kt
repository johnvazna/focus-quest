package com.johnvazna.focusquest.feature.focussession.impl.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusSessionDaoTest {
    private lateinit var database: FocusQuestDatabase
    private lateinit var dao: FocusSessionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FocusQuestDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.focusSessionDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAllowsOnlyOneActiveSession() = runTest {
        val firstInsert = dao.insertIfNoneActive(entity(durationMinutes = 25))
        val secondInsert = dao.insertIfNoneActive(entity(durationMinutes = 30))

        assertNotEquals(-1L, firstInsert)
        assertEquals(-1L, secondInsert)
        assertEquals(25, dao.getActiveSession()?.plannedDurationMinutes)
    }

    private fun entity(durationMinutes: Int) = FocusSessionEntity(
        plannedDurationMinutes = durationMinutes,
        remainingDurationMinutes = durationMinutes,
        pauseCount = 0,
        status = "ACTIVE",
        earnedExperiencePoints = 0,
    )
}

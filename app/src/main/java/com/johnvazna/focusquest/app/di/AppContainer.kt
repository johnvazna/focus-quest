package com.johnvazna.focusquest.app.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import com.johnvazna.focusquest.feature.focussession.impl.data.local.FocusQuestDatabase
import com.johnvazna.focusquest.feature.focussession.impl.data.repository.RoomFocusSessionRepository
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSession
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionViewModel
import com.johnvazna.focusquest.feature.focussession.impl.presentation.MinuteFocusSessionTicker

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        FocusQuestDatabase::class.java,
        DATABASE_NAME,
    ).build()

    private val focusSessionRepository = RoomFocusSessionRepository(database.focusSessionDao())

    val focusSessionViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            FocusSessionViewModel(
                startFocusSession = StartFocusSession(focusSessionRepository),
                ticker = MinuteFocusSessionTicker(),
            )
        }
    }

    private companion object {
        const val DATABASE_NAME = "focus-quest.db"
    }
}

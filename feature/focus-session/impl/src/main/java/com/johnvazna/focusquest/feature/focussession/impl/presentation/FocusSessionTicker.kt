package com.johnvazna.focusquest.feature.focussession.impl.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun interface FocusSessionTicker {
    fun ticks(): Flow<Unit>
}

class MinuteFocusSessionTicker : FocusSessionTicker {
    override fun ticks(): Flow<Unit> = flow {
        while (true) {
            delay(60_000)
            emit(Unit)
        }
    }
}

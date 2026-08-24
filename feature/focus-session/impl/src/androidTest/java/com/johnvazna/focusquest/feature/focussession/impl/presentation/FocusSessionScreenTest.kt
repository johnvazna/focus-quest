package com.johnvazna.focusquest.feature.focussession.impl.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusSessionScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun idleStateShowsDurationOptionsAndEmitsStart() {
        var receivedEvent: FocusSessionUiEvent? = null
        composeRule.setContent {
            FocusQuestTheme(dynamicColor = false) {
                FocusSessionScreen(
                    state = FocusSessionUiState(),
                    onEvent = { receivedEvent = it },
                )
            }
        }

        composeRule.onNodeWithText("25 min").assertIsDisplayed()
        composeRule.onNodeWithText("Start focus").performClick()

        assertEquals(FocusSessionUiEvent.Start, receivedEvent)
    }

    @Test
    fun pausedStateShowsResumeAndRemainingTime() {
        composeRule.setContent {
            FocusQuestTheme(dynamicColor = false) {
                FocusSessionScreen(
                    state = FocusSessionUiState(
                        remainingMinutes = 12,
                        pauseCount = 1,
                        status = FocusSessionUiStatus.PAUSED,
                    ),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("12 minutes remaining").assertIsDisplayed()
        composeRule.onNodeWithText("Resume").assertIsDisplayed()
        composeRule.onNodeWithText("Pauses: 1 of 3").assertIsDisplayed()
    }

    @Test
    fun completedStateShowsConfirmation() {
        composeRule.setContent {
            FocusQuestTheme(dynamicColor = false) {
                FocusSessionScreen(
                    state = FocusSessionUiState(status = FocusSessionUiStatus.COMPLETED),
                    onEvent = {},
                )
            }
        }

        composeRule.onNodeWithText("Session completed").assertIsDisplayed()
    }
}

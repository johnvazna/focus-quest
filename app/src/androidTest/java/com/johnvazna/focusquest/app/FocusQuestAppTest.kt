package com.johnvazna.focusquest.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.johnvazna.focusquest.feature.focussession.impl.domain.repository.FocusSessionRepository
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSession
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionTicker
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test

class FocusQuestAppTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun displaysDashboardAsInitialDestination() {
        composeRule.setContent {
            FocusQuestApp(focusSessionViewModelFactory = focusSessionViewModelFactory())
        }

        composeRule.onNodeWithText("Nothing in motion yet").assertIsDisplayed()
    }

    @Test
    fun projectTabOpensFocusSession() {
        composeRule.setContent {
            FocusQuestApp(focusSessionViewModelFactory = focusSessionViewModelFactory())
        }

        composeRule.onNodeWithText("Project").performClick()

        composeRule.onNodeWithText("Focus session").assertIsDisplayed()
    }

    private fun focusSessionViewModelFactory() = viewModelFactory {
        initializer {
            FocusSessionViewModel(
                startFocusSession = StartFocusSession(FocusSessionRepository { true }),
                ticker = FocusSessionTicker { emptyFlow() },
            )
        }
    }
}

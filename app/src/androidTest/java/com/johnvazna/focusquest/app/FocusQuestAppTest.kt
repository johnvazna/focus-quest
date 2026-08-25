package com.johnvazna.focusquest.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.johnvazna.focusquest.feature.focussession.impl.domain.repository.FocusSessionRepository
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSession
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionTicker
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionViewModel
import com.johnvazna.focusquest.core.designsystem.component.FOCUS_QUEST_PROGRESS_FIELD_TAG
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
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
    fun projectTabOpensProjectEmptyState() {
        composeRule.setContent {
            FocusQuestApp(focusSessionViewModelFactory = focusSessionViewModelFactory())
        }

        composeRule.onNodeWithText("Project").performClick()

        composeRule.onAllNodesWithText("Project").assertCountEquals(2)
    }

    @Test
    fun dashboardCreateActionOpensProjectEmptyState() {
        composeRule.setContent {
            FocusQuestApp(focusSessionViewModelFactory = focusSessionViewModelFactory())
        }

        composeRule.onNodeWithText("Create a project").performClick()

        composeRule.onAllNodesWithText("Project").assertCountEquals(2)
    }

    @Test
    fun switchingEmptyTabsKeepsCanvasAndActionComposed() {
        composeRule.setContent {
            FocusQuestApp(focusSessionViewModelFactory = focusSessionViewModelFactory())
        }
        val canvasId = composeRule
            .onNodeWithTag(FOCUS_QUEST_PROGRESS_FIELD_TAG, useUnmergedTree = true)
            .fetchSemanticsNode().id
        val actionId = composeRule.onNodeWithText("Create a project").fetchSemanticsNode().id

        composeRule.onNodeWithText("Project").performClick()

        assertEquals(
            canvasId,
            composeRule.onNodeWithTag(
                FOCUS_QUEST_PROGRESS_FIELD_TAG,
                useUnmergedTree = true,
            ).fetchSemanticsNode().id,
        )
        assertEquals(
            actionId,
            composeRule.onNodeWithText("Create a project").fetchSemanticsNode().id,
        )

        composeRule.onNodeWithText("Timeline").performClick()

        composeRule.onAllNodesWithText("Timeline").assertCountEquals(2)
        assertEquals(
            canvasId,
            composeRule.onNodeWithTag(
                FOCUS_QUEST_PROGRESS_FIELD_TAG,
                useUnmergedTree = true,
            ).fetchSemanticsNode().id,
        )
        assertEquals(
            actionId,
            composeRule.onNodeWithText("Create a project").fetchSemanticsNode().id,
        )
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

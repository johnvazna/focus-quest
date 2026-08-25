package com.johnvazna.focusquest.feature.project.impl.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import com.johnvazna.focusquest.core.designsystem.component.FOCUS_QUEST_PROGRESS_FIELD_TAG
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyProjectExplainsTheNextStep() {
        setContent()

        composeRule.onNodeWithText("Project").assertIsDisplayed()
        composeRule.onNodeWithText("Nothing in motion yet").assertIsDisplayed()
        composeRule.onNodeWithText("Create a project").assertIsDisplayed()
        composeRule.onNodeWithTag(FOCUS_QUEST_PROGRESS_FIELD_TAG, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun createProjectActionIsForwarded() {
        var createRequested = false
        composeRule.setContent {
            FocusQuestTheme {
                ProjectScreen(onCreateProject = { createRequested = true })
            }
        }

        composeRule.onNodeWithText("Create a project").performClick()

        assertTrue(createRequested)
    }

    private fun setContent() {
        composeRule.setContent {
            FocusQuestTheme {
                ProjectScreen(onCreateProject = {})
            }
        }
    }
}

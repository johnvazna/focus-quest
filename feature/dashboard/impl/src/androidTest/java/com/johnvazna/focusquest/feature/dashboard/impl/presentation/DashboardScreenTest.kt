package com.johnvazna.focusquest.feature.dashboard.impl.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun withoutAProjectTheScreenExplainsWhatIsMissing() {
        setContent()

        composeRule.onNodeWithText("Dashboard").assertIsDisplayed()
        composeRule.onNodeWithText("Nothing in motion yet").assertIsDisplayed()
        composeRule
            .onNodeWithText("One project at a time", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun theEmptyStateShowsNoMetric() {
        setContent()

        composeRule.onNodeWithText("0%").assertDoesNotExist()
        composeRule.onNodeWithText("Week", substring = true).assertDoesNotExist()
    }

    @Test
    fun theRestingFieldIsNotAnnouncedAsAProgressIndicator() {
        setContent()

        composeRule.onNodeWithTag(PROJECT_PROGRESS_FIELD_TAG, useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun theOnlyActionCreatesAProject() {
        var created = false
        composeRule.setContent {
            FocusQuestTheme {
                DashboardScreen(onCreateProject = { created = true })
            }
        }

        composeRule.onNodeWithText("Create a project").performClick()

        assertTrue(created)
    }

    private fun setContent() {
        composeRule.setContent {
            FocusQuestTheme {
                DashboardScreen(onCreateProject = {})
            }
        }
    }
}

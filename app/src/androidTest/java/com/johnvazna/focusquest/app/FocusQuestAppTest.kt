package com.johnvazna.focusquest.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class FocusQuestAppTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun displaysAppName() {
        composeRule.setContent {
            FocusQuestApp()
        }

        composeRule.onNodeWithText("FocusQuest").assertIsDisplayed()
    }
}

package org.example.pixnoa.ui.screen.converter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import kotlin.test.Test

class ConverterScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // ファイルを開くボタンが表示されること
    @Test
    fun converterScreen_displaysOpenFileButton() {
        composeTestRule.setContent { converterScreen() }

        composeTestRule.onNodeWithText("ファイルを開く").assertIsDisplayed()
    }
}

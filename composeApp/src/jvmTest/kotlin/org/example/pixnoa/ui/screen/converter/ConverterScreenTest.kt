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

    // 閉じるボタンが表示されること
    @Test
    fun converterScreen_displaysCloseButton() {
        composeTestRule.setContent { converterScreen() }

        composeTestRule.onNodeWithText("閉じる").assertIsDisplayed()
    }

    // 初期状態では画像未選択のプレースホルダーが表示されること
    @Test
    fun converterScreen_displaysPlaceholderInitially() {
        composeTestRule.setContent { converterScreen() }

        composeTestRule.onNodeWithText("画像が選択されていません").assertIsDisplayed()
    }
}

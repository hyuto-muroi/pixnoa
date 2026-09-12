package org.example.pixnoa.ui.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.example.pixnoa.domain.model.PixelArtConfig
import org.junit.Rule
import kotlin.test.Test

class ParameterPanelTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // 渡されたconfigのdotSize・colorCountがそれぞれ表示されること
    @Test
    fun parameterPanel_displaysDotSizeAndColorCountFromConfig() {
        composeTestRule.setContent {
            parameterPanel(
                config = PixelArtConfig(dotSize = 12, colorCount = 48),
                onDotSizeChange = {},
                onColorCountChange = {},
            )
        }

        composeTestRule.onNodeWithText("ドットサイズ: 12").assertIsDisplayed()
        composeTestRule.onNodeWithText("色数: 48").assertIsDisplayed()
    }
}

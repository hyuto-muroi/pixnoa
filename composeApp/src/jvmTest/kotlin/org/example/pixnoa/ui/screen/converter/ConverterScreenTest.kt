package org.example.pixnoa.ui.screen.converter

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult
import org.example.pixnoa.domain.repository.ImageRepository
import org.example.pixnoa.domain.repository.PixelArtConverter
import org.example.pixnoa.domain.usecase.ConvertToPixelArtUseCase
import org.example.pixnoa.domain.usecase.LoadImageUseCase
import org.junit.Rule
import kotlin.test.Test

class ConverterScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModelStoreOwner =
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }

    private val lifecycleOwner =
        object : LifecycleOwner {
            val registry = LifecycleRegistry(this)
            override val lifecycle: Lifecycle get() = registry
        }

    private val loadImageUseCase =
        LoadImageUseCase(
            object : ImageRepository {
                override fun load(path: String): ByteArray = byteArrayOf()

                override fun save(
                    bytes: ByteArray,
                    path: String,
                ) = Unit
            },
        )

    private val convertToPixelArtUseCase =
        ConvertToPixelArtUseCase(
            object : PixelArtConverter {
                override fun convert(
                    imageBytes: ByteArray,
                    config: PixelArtConfig,
                ): PixelArtResult = PixelArtResult(byteArrayOf(), 0, 0)
            },
        )

    private fun setConverterScreenContent() {
        composeTestRule.runOnUiThread {
            lifecycleOwner.registry.currentState = Lifecycle.State.RESUMED
        }
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalLifecycleOwner provides lifecycleOwner,
            ) {
                converterScreen(loadImageUseCase, convertToPixelArtUseCase)
            }
        }
    }

    // ファイルを開くボタンが表示されること
    @Test
    fun converterScreen_displaysOpenFileButton() {
        setConverterScreenContent()

        composeTestRule.onNodeWithText("ファイルを開く").assertIsDisplayed()
    }

    // クリアボタンが表示されること
    @Test
    fun converterScreen_displaysClearButton() {
        setConverterScreenContent()

        composeTestRule.onNodeWithText("クリア").assertIsDisplayed()
    }

    // 初期状態では画像未選択のプレースホルダーが表示されること
    @Test
    fun converterScreen_displaysPlaceholderInitially() {
        setConverterScreenContent()

        composeTestRule.onNodeWithText("画像が選択されていません").assertIsDisplayed()
    }
}

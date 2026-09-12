package org.example.pixnoa.ui.screen.converter

import kotlinx.coroutines.Dispatchers
import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult
import org.example.pixnoa.domain.repository.ImageRepository
import org.example.pixnoa.domain.repository.PixelArtConverter
import org.example.pixnoa.domain.usecase.ConvertToPixelArtUseCase
import org.example.pixnoa.domain.usecase.LoadImageUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConverterViewModelTest {
    // 読み込み・変換に成功した場合、元画像・変換後画像が反映されisConvertingがfalseに戻ること
    @Test
    fun onImageSelected_whenSucceeds_updatesImagesAndStopsConverting() {
        val original = byteArrayOf(1, 2, 3)
        val converted = PixelArtResult(byteArrayOf(4, 5, 6), width = 10, height = 20)
        val viewModel = createViewModel(load = { original }, convert = { _, _ -> converted })

        viewModel.onImageSelected("sample/path.png")

        val state = viewModel.uiState.value
        assertEquals(original, state.originalImage)
        assertEquals(converted.imageBytes, state.convertedImage)
        assertFalse(state.isConverting)
        assertNull(state.error)
    }

    // 読み込みに失敗した場合、errorに反映されisConvertingがfalseに戻ること
    @Test
    fun onImageSelected_whenLoadFails_setsErrorAndStopsConverting() {
        val viewModel = createViewModel(load = { throw IllegalStateException("読み込み失敗") })

        viewModel.onImageSelected("sample/path.png")

        val state = viewModel.uiState.value
        assertFalse(state.isConverting)
        assertTrue(state.error?.contains("読み込み失敗") == true)
    }

    // 変換に失敗した場合、errorに反映されisConvertingがfalseに戻ること
    @Test
    fun onImageSelected_whenConvertFails_setsErrorAndStopsConverting() {
        val viewModel =
            createViewModel(
                load = { byteArrayOf(1) },
                convert = { _, _ -> throw IllegalStateException("変換失敗") },
            )

        viewModel.onImageSelected("sample/path.png")

        val state = viewModel.uiState.value
        assertFalse(state.isConverting)
        assertTrue(state.error?.contains("変換失敗") == true)
    }

    // onClearを呼ぶと状態が初期化されること
    @Test
    fun onClear_resetsStateToInitial() {
        val viewModel =
            createViewModel(
                load = { byteArrayOf(1) },
                convert = { _, _ -> PixelArtResult(byteArrayOf(2), width = 1, height = 1) },
            )
        viewModel.onImageSelected("sample/path.png")

        viewModel.onClear()

        assertEquals(ConverterState(), viewModel.uiState.value)
    }

    private fun createViewModel(
        load: (String) -> ByteArray = { byteArrayOf() },
        convert: (ByteArray, PixelArtConfig) -> PixelArtResult = { _, _ -> PixelArtResult(byteArrayOf(), 0, 0) },
    ): ConverterViewModel {
        val imageRepository =
            object : ImageRepository {
                override fun load(path: String): ByteArray = load(path)

                override fun save(
                    bytes: ByteArray,
                    path: String,
                ) {
                    error("このテストでは使用されない想定")
                }
            }
        val converter =
            object : PixelArtConverter {
                override fun convert(
                    imageBytes: ByteArray,
                    config: PixelArtConfig,
                ): PixelArtResult = convert(imageBytes, config)
            }

        return ConverterViewModel(
            loadImageUseCase = LoadImageUseCase(imageRepository),
            convertToPixelArtUseCase = ConvertToPixelArtUseCase(converter),
            dispatcher = Dispatchers.Unconfined,
        )
    }
}

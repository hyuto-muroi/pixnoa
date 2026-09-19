package org.example.pixnoa.domain.usecase

import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult
import org.example.pixnoa.domain.repository.PixelArtConverter
import kotlin.test.Test
import kotlin.test.assertSame

class ConvertToPixelArtUseCaseTest {
    // executeがconverter.convertに引数をそのまま渡し、その結果をそのまま返すこと
    @Test
    fun execute_delegatesToConverterAndReturnsItsResult() {
        var receivedImageBytes: ByteArray? = null
        var receivedConfig: PixelArtConfig? = null
        val expected = PixelArtResult(byteArrayOf(1, 2, 3), width = 10, height = 20)
        val converter =
            object : PixelArtConverter {
                override fun convert(
                    imageBytes: ByteArray,
                    config: PixelArtConfig,
                ): PixelArtResult {
                    receivedImageBytes = imageBytes
                    receivedConfig = config
                    return expected
                }
            }
        val useCase = ConvertToPixelArtUseCase(converter)

        val imageBytes = byteArrayOf(4, 5, 6)
        val config = PixelArtConfig(dotSize = 4, colorCount = 8)
        val result = useCase.execute(imageBytes, config)

        assertSame(expected, result)
        assertSame(imageBytes, receivedImageBytes)
        assertSame(config, receivedConfig)
    }
}

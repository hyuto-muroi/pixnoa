package org.example.pixnoa.data.repository

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.opencv.global.opencv_core.CV_8UC1
import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR
import org.bytedeco.opencv.global.opencv_imgcodecs.imdecode
import org.bytedeco.opencv.global.opencv_imgcodecs.imencode
import org.bytedeco.opencv.opencv_core.Mat
import org.example.pixnoa.domain.model.PixelArtConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PixelArtConverterImplTest {
    // downscale・quantizeColors・upscaleがこの順番で、configの値を渡して呼ばれること
    @Test
    fun convert_callsTransformationsInOrderWithConfigValues() {
        val calledSteps = mutableListOf<String>()
        var receivedDotSizeForDownscale = -1
        var receivedColorCount = -1
        var receivedDotSizeForUpscale = -1

        val converter =
            PixelArtConverterImpl(
                downscale = { image, dotSize ->
                    calledSteps += "downscale"
                    receivedDotSizeForDownscale = dotSize
                    Mat(image.rows(), image.cols(), image.type())
                },
                upscale = { image, dotSize ->
                    calledSteps += "upscale"
                    receivedDotSizeForUpscale = dotSize
                    Mat(image.rows(), image.cols(), image.type())
                },
                quantizeColors = { image, colorCount ->
                    calledSteps += "quantizeColors"
                    receivedColorCount = colorCount
                    Mat(image.rows(), image.cols(), image.type())
                },
            )

        converter.convert(encodedSampleImage(20, 10), PixelArtConfig(dotSize = 4, colorCount = 8))

        assertEquals(listOf("downscale", "quantizeColors", "upscale"), calledSteps)
        assertEquals(4, receivedDotSizeForDownscale)
        assertEquals(8, receivedColorCount)
        assertEquals(4, receivedDotSizeForUpscale)
    }

    // デコード直後・各変換ステップの前のMatが、最終的にすべて解放されること
    @Test
    fun convert_releasesAllIntermediateMatsIncludingTheFinalOne() {
        lateinit var decodedMat: Mat
        lateinit var downscaledMat: Mat
        lateinit var quantizedMat: Mat
        lateinit var upscaledMat: Mat

        val converter =
            PixelArtConverterImpl(
                downscale = { image, _ ->
                    decodedMat = image
                    Mat(image.rows(), image.cols(), image.type()).also { downscaledMat = it }
                },
                quantizeColors = { image, _ ->
                    Mat(image.rows(), image.cols(), image.type()).also { quantizedMat = it }
                },
                upscale = { image, _ ->
                    Mat(image.rows(), image.cols(), image.type()).also { upscaledMat = it }
                },
            )

        converter.convert(encodedSampleImage(20, 10), PixelArtConfig(dotSize = 2, colorCount = 4))

        assertTrue(decodedMat.empty(), "デコード直後のMatが解放されていません")
        assertTrue(downscaledMat.empty(), "downscale結果のMatが解放されていません")
        assertTrue(quantizedMat.empty(), "quantizeColors結果のMatが解放されていません")
        assertTrue(upscaledMat.empty(), "最終結果のMatが解放されていません")
    }

    // 実際のOpenCvProcessorを使った場合、デコード可能なPNGバイト列と正しいサイズが返ること
    @Test
    fun convert_withRealOpenCvProcessor_returnsDecodableImageWithExpectedSize() {
        val converter = PixelArtConverterImpl()

        val result = converter.convert(encodedSampleImage(200, 100), PixelArtConfig(dotSize = 10, colorCount = 4))

        assertTrue(result.imageBytes.isNotEmpty())
        assertEquals(200, result.width)
        assertEquals(100, result.height)

        val (decodedWidth, decodedHeight) = decodedSize(result.imageBytes)
        assertEquals(200, decodedWidth)
        assertEquals(100, decodedHeight)
    }

    private fun encodedSampleImage(
        width: Int,
        height: Int,
    ): ByteArray {
        val mat = Mat(height, width, CV_8UC3)
        val buf = BytePointer()
        imencode(".png", mat, buf)
        val bytes = ByteArray(buf.limit().toInt())
        buf.get(bytes)
        mat.release()
        buf.deallocate()
        return bytes
    }

    private fun decodedSize(bytes: ByteArray): Pair<Int, Int> {
        val bytePointer = BytePointer(*bytes)
        val srcMat = Mat(bytes.size, 1, CV_8UC1, bytePointer)
        val decoded = imdecode(srcMat, IMREAD_COLOR)
        val size = decoded.cols() to decoded.rows()
        srcMat.release()
        bytePointer.deallocate()
        decoded.release()
        return size
    }
}

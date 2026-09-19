package org.example.pixnoa.data.source

import org.bytedeco.javacpp.indexer.UByteIndexer
import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.global.opencv_core.CV_8UC4
import org.bytedeco.opencv.opencv_core.Mat
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OpenCvProcessorTest {
    private lateinit var sampleImage: Mat
    private lateinit var result: Mat

    @BeforeTest
    fun setUp() {
        sampleImage = Mat(IMAGE_HEIGHT, IMAGE_WIDTH, CV_8UC3)
    }

    @AfterTest
    fun tearDown() {
        if (::sampleImage.isInitialized) sampleImage.release()
        if (::result.isInitialized) result.release()
    }

    // dotSizeで幅と高さをそれぞれ割ったサイズに縮小されること
    @Test
    fun downscale_withValidDotSize_returnsDownscaledSize() {
        result = OpenCvProcessor.downscale(sampleImage, 2)
        assertEquals(IMAGE_WIDTH / 2, result.cols())
        assertEquals(IMAGE_HEIGHT / 2, result.rows())
    }

    // dotSizeが1の場合、元の画像と同じサイズが返ること
    @Test
    fun downscale_withDotSizeOne_returnsSameSize() {
        result = OpenCvProcessor.downscale(sampleImage, 1)
        assertEquals(IMAGE_WIDTH, result.cols())
        assertEquals(IMAGE_HEIGHT, result.rows())
    }

    // 画像サイズがdotSizeで割り切れない場合、切り捨てたサイズが返ること
    @Test
    fun downscale_whenSizeNotDivisible_returnsTruncatedSize() {
        result = OpenCvProcessor.downscale(sampleImage, 3)
        assertEquals(IMAGE_WIDTH / 3, result.cols())
        assertEquals(IMAGE_HEIGHT / 3, result.rows())
    }

    // dotSizeが画像サイズより大きい場合、IllegalArgumentExceptionがスローされること
    @Test
    fun downscale_whenDotSizeLargerThanImage_throwsIllegalArgumentException() {
        sampleImage.release()
        sampleImage = Mat(1, 1, CV_8UC3)
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.downscale(sampleImage, 2) }
    }

    // 空の画像の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun downscale_withEmptyImage_throwsIllegalArgumentException() {
        sampleImage.release()
        sampleImage = Mat()
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.downscale(sampleImage, 1) }
    }

    // dotSizeが0の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun downscale_withZeroDotSize_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.downscale(sampleImage, 0) }
    }

    // dotSizeが負の数の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun downscale_withNegativeDotSize_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.downscale(sampleImage, -1) }
    }

    // dotSizeで幅と高さをそれぞれ掛けたサイズに拡大されること
    @Test
    fun upscale_withValidDotSize_returnsUpscaledSize() {
        result = OpenCvProcessor.upscale(sampleImage, 2)
        assertEquals(IMAGE_WIDTH * 2, result.cols())
        assertEquals(IMAGE_HEIGHT * 2, result.rows())
    }

    // dotSizeが1の場合、元の画像と同じサイズが返ること
    @Test
    fun upscale_withDotSizeOne_returnsSameSize() {
        result = OpenCvProcessor.upscale(sampleImage, 1)
        assertEquals(IMAGE_WIDTH, result.cols())
        assertEquals(IMAGE_HEIGHT, result.rows())
    }

    // colorCountで指定した色数に削減された画像が元の画像と同じサイズで返ること
    @Test
    fun quantizeColors_withValidColorCount_returnsSameSizeImage() {
        result = OpenCvProcessor.quantizeColors(sampleImage, 8)
        assertEquals(IMAGE_WIDTH, result.cols())
        assertEquals(IMAGE_HEIGHT, result.rows())
        assertEquals(sampleImage.type(), result.type())
    }

    // colorCountが1の場合、元の画像と同じサイズの画像が返ること
    @Test
    fun quantizeColors_withColorCountOne_returnsSameSizeImage() {
        result = OpenCvProcessor.quantizeColors(sampleImage, 1)
        assertEquals(IMAGE_WIDTH, result.cols())
        assertEquals(IMAGE_HEIGHT, result.rows())
    }

    // 空の画像の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun upscale_withEmptyImage_throwsIllegalArgumentException() {
        sampleImage.release()
        sampleImage = Mat()
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.upscale(sampleImage, 1) }
    }

    // dotSizeが0の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun upscale_withZeroDotSize_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.upscale(sampleImage, 0) }
    }

    // dotSizeが負の数の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun upscale_withNegativeDotSize_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.upscale(sampleImage, -1) }
    }

    // colorCountがピクセル数より大きい場合、IllegalArgumentExceptionがスローされること
    @Test
    fun quantizeColors_whenColorCountExceedsPixelCount_throwsIllegalArgumentException() {
        sampleImage.release()
        sampleImage = Mat(1, 1, CV_8UC3)
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.quantizeColors(sampleImage, 2) }
    }

    // 空の画像の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun quantizeColors_withEmptyImage_throwsIllegalArgumentException() {
        sampleImage.release()
        sampleImage = Mat()
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.quantizeColors(sampleImage, 8) }
    }

    // colorCountが0の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun quantizeColors_withZeroColorCount_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.quantizeColors(sampleImage, 0) }
    }

    // colorCountが負の数の場合、IllegalArgumentExceptionがスローされること
    @Test
    fun quantizeColors_withNegativeColorCount_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { OpenCvProcessor.quantizeColors(sampleImage, -1) }
    }

    // 4チャンネル(透過あり)画像の場合、量子化後もサイズと4チャンネルが維持されること
    @Test
    fun quantizeColors_withFourChannelImage_returnsSameSizeFourChannelImage() {
        val fourChannelImage = Mat(IMAGE_HEIGHT, IMAGE_WIDTH, CV_8UC4)

        result = OpenCvProcessor.quantizeColors(fourChannelImage, 8)

        assertEquals(IMAGE_WIDTH, result.cols())
        assertEquals(IMAGE_HEIGHT, result.rows())
        assertEquals(4, result.channels())

        fourChannelImage.release()
    }

    // 4チャンネル(透過あり)画像の場合、アルファチャンネルは量子化されず元の値のまま保持されること
    @Test
    fun quantizeColors_withFourChannelImage_preservesAlphaChannel() {
        val fourChannelImage = Mat(IMAGE_HEIGHT, IMAGE_WIDTH, CV_8UC4)
        val sourceIndexer = fourChannelImage.createIndexer<UByteIndexer>()
        for (r in 0 until IMAGE_HEIGHT) {
            for (c in 0 until IMAGE_WIDTH) {
                val alpha = if (c < IMAGE_WIDTH / 2) 255 else 128
                sourceIndexer.put(r.toLong(), c.toLong(), 0L, 100)
                sourceIndexer.put(r.toLong(), c.toLong(), 1L, 150)
                sourceIndexer.put(r.toLong(), c.toLong(), 2L, 200)
                sourceIndexer.put(r.toLong(), c.toLong(), 3L, alpha)
            }
        }

        result = OpenCvProcessor.quantizeColors(fourChannelImage, 4)

        val resultIndexer = result.createIndexer<UByteIndexer>()
        assertEquals(255, resultIndexer.get(0L, 0L, 3L))
        assertEquals(128, resultIndexer.get(0L, (IMAGE_WIDTH - 1).toLong(), 3L))

        fourChannelImage.release()
    }

    companion object {
        const val IMAGE_WIDTH = 200
        const val IMAGE_HEIGHT = 100
    }
}

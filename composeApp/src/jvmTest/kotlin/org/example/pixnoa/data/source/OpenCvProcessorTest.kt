package org.example.pixnoa.data.source

import org.bytedeco.opencv.global.opencv_core.CV_8UC3
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

    companion object {
        const val IMAGE_WIDTH = 200
        const val IMAGE_HEIGHT = 100
    }
}

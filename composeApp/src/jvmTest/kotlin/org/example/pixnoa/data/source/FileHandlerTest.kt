package org.example.pixnoa.data.source

import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.opencv_core.Mat
import java.io.File
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FileHandlerTest {
    private lateinit var sampleFile: File
    private lateinit var sampleFilePath: String

    @BeforeTest
    fun setUp() {
        val mat = Mat(100, 200, CV_8UC3)
        sampleFile =
            File.createTempFile("test_image", ".png").also {
                imwrite(it.path, mat)
                mat.release()
            }

        sampleFilePath = sampleFile.path
    }

    @AfterTest
    fun tearDown() {
        sampleFile.delete()
    }

    // 指定したパスの画像データがByteArray（バイト列）で返されること
    @Test
    fun readBytes_withValidPath_returnsByteArray() {
        val result = FileHandler.readBytes(sampleFilePath)
        assertTrue(result.isNotEmpty())
    }

    // パスが空文字または空白の場合は IllegalArgumentException がスローされること
    @Test
    fun readBytes_pathIsEmptyOrBlank_throwsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> { FileHandler.readBytes(" ") }
        assertFailsWith<IllegalArgumentException> { FileHandler.readBytes("") }
    }

    // ファイルが読み込めない、あるいは存在しない場合は java.io.IOException がスローされること
    @Test
    fun readBytes_theFileCannotBeReadOrDoesNotExist_throwsIOException() {
        assertFailsWith<IOException> { FileHandler.readBytes("/non/existent/path/image.png") }
    }
}

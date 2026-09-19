package org.example.pixnoa.data.source

import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
import org.bytedeco.opencv.opencv_core.Mat
import java.awt.Component
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.swing.JFileChooser
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

    // 指定したパスのファイルの内容がByteArray（バイト列）で返されること
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

    // 指定したパスにファイルが出力されること
    @Test
    fun writeBytes_fileIsSavedToTheSpecifiedPath() {
        val sampleImageData = File(sampleFilePath).readBytes()
        val tmpDir = System.getProperty("java.io.tmpdir")
        val path = "$tmpDir/output_image.png"
        FileHandler.writeBytes(sampleImageData, path)
        assertTrue(File(path).readBytes().isNotEmpty())
    }

    // 出力するファイルのデータが空だった場合は IllegalArgumentException がスローされること
    @Test
    fun writeBytes_dataInTheOutputFileIsEmpty_throwsIllegalArgumentException() {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val path = "$tmpDir/output_image.png"
        assertFailsWith<IllegalArgumentException> { FileHandler.writeBytes(ByteArray(0), path) }
    }

    // 出力先のパスが空文字あるいは空白の場合は IllegalArgumentException がスローされること
    @Test
    fun writeBytes_outputPathIsAnEmptyStringOrContainsOnlySpaces_throwsIllegalArgumentException() {
        val sampleImageData = File(sampleFilePath).readBytes()
        assertFailsWith<IllegalArgumentException> { FileHandler.writeBytes(sampleImageData, "") }
        assertFailsWith<IllegalArgumentException> { FileHandler.writeBytes(sampleImageData, " ") }
    }

    // ファイルの出力に失敗した場合は FileNotFoundException がスローされること
    @Test
    fun writeBytes_fileFailsToBeWritten_throwsFileNotFoundException() {
        val sampleImageData = File(sampleFilePath).readBytes()
        assertFailsWith<FileNotFoundException> {
            FileHandler.writeBytes(
                sampleImageData,
                "/non/existent/path/output.png",
            )
        }
    }

    // ファイルが選択された場合、選択されたファイルのパスが返されること
    @Test
    fun openFileDialog_fileIsSelected_returnsThePathOfTheSelectedFile() {
        val chooser =
            object : JFileChooser() {
                override fun showOpenDialog(parent: Component?): Int {
                    selectedFile = File(sampleFilePath)
                    return APPROVE_OPTION
                }
            }
        assertEquals(sampleFilePath, FileHandler.openFileDialog(chooser))
    }

    // ダイアログがキャンセルされた場合、null が返されること
    @Test
    fun openFileDialog_dialogIsCancelled_returnsNull() {
        val chooser =
            object : JFileChooser() {
                override fun showOpenDialog(parent: Component?): Int = CANCEL_OPTION
            }
        assertNull(FileHandler.openFileDialog(chooser))
    }

    // 保存先が指定された場合、指定されたパスが返されること
    @Test
    fun saveFileDialog_fileIsSelected_returnsThePathOfTheSelectedFile() {
        val chooser =
            object : JFileChooser() {
                override fun showSaveDialog(parent: Component?): Int {
                    selectedFile = File(sampleFilePath)
                    return APPROVE_OPTION
                }
            }
        assertEquals(sampleFilePath, FileHandler.saveFileDialog(chooser, confirmOverwrite = { true }))
    }

    // ダイアログがキャンセルされた場合、null が返されること
    @Test
    fun saveFileDialog_dialogIsCancelled_returnsNull() {
        val chooser =
            object : JFileChooser() {
                override fun showSaveDialog(parent: Component?): Int = CANCEL_OPTION
            }
        assertNull(FileHandler.saveFileDialog(chooser))
    }

    // 選択したファイルが存在しない場合、上書き確認をせずそのままパスが返されること
    @Test
    fun saveFileDialog_whenFileDoesNotExist_doesNotAskForConfirmation() {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val nonExistentFile = File("$tmpDir/does_not_exist_${System.nanoTime()}.png")
        var confirmCalled = false
        val chooser =
            object : JFileChooser() {
                override fun showSaveDialog(parent: Component?): Int {
                    selectedFile = nonExistentFile
                    return APPROVE_OPTION
                }
            }

        val result =
            FileHandler.saveFileDialog(chooser, confirmOverwrite = {
                confirmCalled = true
                true
            })

        assertEquals(nonExistentFile.path, result)
        assertFalse(confirmCalled)
    }

    // 選択したファイルが既に存在し、上書きを許可した場合、そのパスが返されること
    @Test
    fun saveFileDialog_whenFileExistsAndOverwriteConfirmed_returnsPath() {
        val chooser =
            object : JFileChooser() {
                override fun showSaveDialog(parent: Component?): Int {
                    selectedFile = File(sampleFilePath)
                    return APPROVE_OPTION
                }
            }

        val result = FileHandler.saveFileDialog(chooser, confirmOverwrite = { true })

        assertEquals(sampleFilePath, result)
    }

    // 選択したファイルが既に存在し、上書きを拒否した場合、ダイアログが再表示されること
    @Test
    fun saveFileDialog_whenOverwriteDeclined_showsDialogAgain() {
        var showDialogCallCount = 0
        var confirmCallCount = 0
        val chooser =
            object : JFileChooser() {
                override fun showSaveDialog(parent: Component?): Int {
                    showDialogCallCount++
                    selectedFile = File(sampleFilePath)
                    return APPROVE_OPTION
                }
            }

        val result =
            FileHandler.saveFileDialog(chooser) {
                confirmCallCount++
                confirmCallCount > 1
            }

        assertEquals(sampleFilePath, result)
        assertEquals(2, showDialogCallCount)
        assertEquals(2, confirmCallCount)
    }
}

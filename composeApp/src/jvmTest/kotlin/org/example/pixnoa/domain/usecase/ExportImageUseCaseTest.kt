package org.example.pixnoa.domain.usecase

import org.example.pixnoa.domain.repository.ImageRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ExportImageUseCaseTest {
    // executeがimageRepository.saveに指定したバイト列を渡すこと
    @Test
    fun execute_delegatesToImageRepositoryWithGivenImageBytes() {
        var receivedBytes: ByteArray? = null
        val imageBytes = byteArrayOf(1, 2, 3)

        execute(imageBytes = imageBytes, savePath = "output/path.png", onSave = { bytes, _ -> receivedBytes = bytes })

        assertSame(imageBytes, receivedBytes)
    }

    // パスが既に.png拡張子を持つ場合、そのままのパスで保存されること
    @Test
    fun execute_whenPathAlreadyHasPngExtension_savesWithSamePath() {
        var receivedPath: String? = null

        execute(savePath = "output/path.png", onSave = { _, path -> receivedPath = path })

        assertEquals("output/path.png", receivedPath)
    }

    // パスに拡張子がない場合、.pngが付与されること
    @Test
    fun execute_whenPathHasNoExtension_appendsPngExtension() {
        var receivedPath: String? = null

        execute(savePath = "output/path", onSave = { _, path -> receivedPath = path })

        assertEquals("output/path.png", receivedPath)
    }

    // パスの拡張子が大文字小文字違いの.PNGの場合、重ねて付与されないこと
    @Test
    fun execute_whenPathHasPngExtensionInDifferentCase_doesNotAppendAgain() {
        var receivedPath: String? = null

        execute(savePath = "output/path.PNG", onSave = { _, path -> receivedPath = path })

        assertEquals("output/path.PNG", receivedPath)
    }

    private fun execute(
        imageBytes: ByteArray = byteArrayOf(1, 2, 3),
        savePath: String,
        onSave: (ByteArray, String) -> Unit,
    ) {
        val imageRepository =
            object : ImageRepository {
                override fun load(path: String): ByteArray = error("このテストでは使用されない想定")

                override fun save(
                    bytes: ByteArray,
                    path: String,
                ) = onSave(bytes, path)
            }

        ExportImageUseCase(imageRepository).execute(imageBytes, savePath)
    }
}

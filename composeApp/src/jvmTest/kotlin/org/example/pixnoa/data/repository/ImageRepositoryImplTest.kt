package org.example.pixnoa.data.repository

import org.example.pixnoa.domain.repository.ImageRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ImageRepositoryImplTest {
    // loadが指定したパスでreadBytesを呼び、その結果をそのまま返すこと
    @Test
    fun load_delegatesToReadBytesAndReturnsItsResult() {
        var receivedPath: String? = null
        val expected = byteArrayOf(1, 2, 3)
        val repository: ImageRepository =
            ImageRepositoryImpl(
                readBytes = { path ->
                    receivedPath = path
                    expected
                },
            )

        val result = repository.load("sample/path.png")

        assertEquals("sample/path.png", receivedPath)
        assertSame(expected, result)
    }

    // saveが指定したbytesとpathでwriteBytesを呼ぶこと
    @Test
    fun save_delegatesToWriteBytesWithGivenArguments() {
        var receivedBytes: ByteArray? = null
        var receivedPath: String? = null
        val repository: ImageRepository =
            ImageRepositoryImpl(
                writeBytes = { bytes, path ->
                    receivedBytes = bytes
                    receivedPath = path
                },
            )

        val bytes = byteArrayOf(4, 5, 6)
        repository.save(bytes, "output/path.png")

        assertSame(bytes, receivedBytes)
        assertEquals("output/path.png", receivedPath)
    }
}

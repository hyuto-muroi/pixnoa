package org.example.pixnoa.domain.usecase

import org.example.pixnoa.domain.repository.ImageRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LoadImageUseCaseTest {
    // executeがimageRepository.loadに指定したパスを渡し、その結果をそのまま返すこと
    @Test
    fun execute_delegatesToImageRepositoryAndReturnsItsResult() {
        var receivedPath: String? = null
        val expected = byteArrayOf(1, 2, 3)
        val imageRepository =
            object : ImageRepository {
                override fun load(path: String): ByteArray {
                    receivedPath = path
                    return expected
                }

                override fun save(
                    bytes: ByteArray,
                    path: String,
                ) {
                    error("このテストでは使用されない想定")
                }
            }
        val useCase = LoadImageUseCase(imageRepository)

        val result = useCase.execute("sample/path.png")

        assertEquals("sample/path.png", receivedPath)
        assertSame(expected, result)
    }
}

package org.example.pixnoa.data.repository

import org.example.pixnoa.data.source.FileHandler
import org.example.pixnoa.domain.repository.ImageRepository

class ImageRepositoryImpl(
    private val readBytes: (String) -> ByteArray = FileHandler::readBytes,
    private val writeBytes: (ByteArray, String) -> Unit = FileHandler::writeBytes,
) : ImageRepository {
    override fun load(path: String): ByteArray = readBytes(path)

    override fun save(
        bytes: ByteArray,
        path: String,
    ) = writeBytes(bytes, path)
}

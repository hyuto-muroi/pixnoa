package org.example.pixnoa.domain.repository

interface ImageRepository {
    fun load(path: String): ByteArray

    fun save(
        bytes: ByteArray,
        path: String,
    )
}

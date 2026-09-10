package org.example.pixnoa.domain.repository

import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult

interface PixelArtConverter {
    fun convert(
        imageBytes: ByteArray,
        config: PixelArtConfig,
    ): PixelArtResult
}

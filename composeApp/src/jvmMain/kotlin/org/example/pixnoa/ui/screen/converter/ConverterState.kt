package org.example.pixnoa.ui.screen.converter

import org.example.pixnoa.domain.model.PixelArtConfig

data class ConverterState(
    val originalImage: ByteArray? = null,
    val convertedImage: ByteArray? = null,
    val config: PixelArtConfig = PixelArtConfig(),
    val isConverting: Boolean = false,
    val error: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConverterState

        if (isConverting != other.isConverting) return false
        if (!originalImage.contentEquals(other.originalImage)) return false
        if (!convertedImage.contentEquals(other.convertedImage)) return false
        if (config != other.config) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isConverting.hashCode()
        result = 31 * result + (originalImage?.contentHashCode() ?: 0)
        result = 31 * result + (convertedImage?.contentHashCode() ?: 0)
        result = 31 * result + config.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}

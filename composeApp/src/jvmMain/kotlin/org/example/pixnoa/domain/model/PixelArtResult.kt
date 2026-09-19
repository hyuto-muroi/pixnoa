package org.example.pixnoa.domain.model

data class PixelArtResult(
    val imageBytes: ByteArray,
    val width: Int,
    val height: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PixelArtResult

        if (width != other.width) return false
        if (height != other.height) return false
        if (!imageBytes.contentEquals(other.imageBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + imageBytes.contentHashCode()
        return result
    }
}

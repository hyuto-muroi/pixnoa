package org.example.pixnoa.data.repository

import org.bytedeco.javacpp.BytePointer
import org.bytedeco.opencv.global.opencv_core.CV_8UC1
import org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR
import org.bytedeco.opencv.global.opencv_imgcodecs.imdecode
import org.bytedeco.opencv.global.opencv_imgcodecs.imencode
import org.bytedeco.opencv.opencv_core.Mat
import org.example.pixnoa.data.source.OpenCvProcessor
import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult
import org.example.pixnoa.domain.repository.PixelArtConverter

class PixelArtConverterImpl(
    private val downscale: (image: Mat, dotSize: Int) -> Mat = OpenCvProcessor::downscale,
    private val upscale: (image: Mat, dotSize: Int) -> Mat = OpenCvProcessor::upscale,
    private val quantizeColors: (image: Mat, colorCount: Int) -> Mat = OpenCvProcessor::quantizeColors,
) : PixelArtConverter {
    override fun convert(
        imageBytes: ByteArray,
        config: PixelArtConfig,
    ): PixelArtResult {
        var matData = decodeToMat(imageBytes)
        matData = matData.releaseAfter { downscale(it, config.dotSize) }
        matData = matData.releaseAfter { quantizeColors(it, config.colorCount) }
        matData = matData.releaseAfter { upscale(it, config.dotSize) }

        val encodedBytes = encodeToBytes(matData)
        val width = matData.cols()
        val height = matData.rows()
        matData.release()

        return PixelArtResult(encodedBytes, width, height)
    }
}

/** バイト列をデコードして [Mat] に変換する。変換用に確保した中間データは内部で解放する */
private fun decodeToMat(imageBytes: ByteArray): Mat {
    val bytePointer = BytePointer(*imageBytes)
    val srcMat = Mat(imageBytes.size, 1, CV_8UC1, bytePointer)
    val decoded = imdecode(srcMat, IMREAD_COLOR)
    srcMat.release()
    bytePointer.deallocate()
    return decoded
}

/** [Mat] をPNG形式のバイト列にエンコードする */
private fun encodeToBytes(mat: Mat): ByteArray {
    val buf = BytePointer()
    imencode(".png", mat, buf)
    val bytes = ByteArray(buf.limit().toInt())
    buf.get(bytes)
    buf.deallocate()
    return bytes
}

/** [transform] の結果を返しつつ、変換前の [Mat] を解放する */
private inline fun Mat.releaseAfter(transform: (Mat) -> Mat): Mat = transform(this).also { release() }

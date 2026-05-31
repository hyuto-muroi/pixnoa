package org.example.pixnoa.data.source

import org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA
import org.bytedeco.opencv.global.opencv_imgproc.resize
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Size

/** OpenCV を使用した画像処理を提供するオブジェクト */
object OpenCvProcessor {
    /**
     * 画像をドットサイズに基づいてダウンスケール
     *
     * @param image ダウンスケールする元の画像
     * @param dotSize ダウンスケールの倍率。1以上の値を指定する
     * @return ダウンスケールされた画像。呼び出し元が [Mat.release] で解放する責任を持つ
     * @throws IllegalArgumentException [dotSize] が0以下の場合、[image] が空の場合、または [dotSize] が画像のサイズより大きい場合
     */
    fun downscale(
        image: Mat,
        dotSize: Int,
    ): Mat {
        if (dotSize <= 0) throw IllegalArgumentException("The dotSize must be greater than zero.")

        if (image.empty()) throw IllegalArgumentException("The image must not be empty.")

        val dstWidth: Int = image.cols() / dotSize
        val dstHeight: Int = image.rows() / dotSize

        if (dstWidth <= 0 || dstHeight <= 0) {
            throw IllegalArgumentException("dotSize ($dotSize) is too large for the image dimensions.")
        }

        val size = Size(dstWidth, dstHeight)
        val dstImg = Mat()

        try {
            resize(image, dstImg, size, 0.0, 0.0, INTER_AREA)
        } catch (e: Exception) {
            dstImg.release()
            throw e
        }

        return dstImg
    }
}

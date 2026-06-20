package org.example.pixnoa.data.source

import org.bytedeco.javacpp.indexer.IntIndexer
import org.bytedeco.javacpp.indexer.UByteIndexer
import org.bytedeco.opencv.global.opencv_core.CV_32F
import org.bytedeco.opencv.global.opencv_core.CV_8U
import org.bytedeco.opencv.global.opencv_core.KMEANS_PP_CENTERS
import org.bytedeco.opencv.global.opencv_core.kmeans
import org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA
import org.bytedeco.opencv.global.opencv_imgproc.INTER_NEAREST
import org.bytedeco.opencv.global.opencv_imgproc.resize
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_core.TermCriteria

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

    /**
     * 画像をドットサイズに基づいてアップスケール
     *
     * @param image アップスケールする元の画像
     * @param dotSize アップスケールの倍率。1以上の値を指定する
     * @return アップスケールされた画像。呼び出し元が [Mat.release] で解放する責任を持つ
     * @throws IllegalArgumentException [dotSize] が0以下の場合
     * @throws IllegalArgumentException [image] が空の場合
     * @throws IllegalArgumentException [dotSize] が画像のサイズより大きい場合
     */
    fun upscale(
        image: Mat,
        dotSize: Int,
    ): Mat {
        if (dotSize <= 0) throw IllegalArgumentException("The dotSize must be greater than zero.")

        if (image.empty()) throw IllegalArgumentException("The image must not be empty.")

        val dstWidth: Int = image.cols() * dotSize
        val dstHeight: Int = image.rows() * dotSize

        if (dstWidth <= 0 || dstHeight <= 0) {
            throw IllegalArgumentException("dotSize ($dotSize) is too large for the image dimensions.")
        }

        val size = Size(dstWidth, dstHeight)
        val dstImg = Mat()

        try {
            resize(image, dstImg, size, 0.0, 0.0, INTER_NEAREST)
        } catch (e: Exception) {
            dstImg.release()
            throw e
        }

        return dstImg
    }

    /**
     * 画像の色を量子化
     *
     * @param image 色を量子化する元の画像
     * @param colorCount 元画像の色を何色に削減するかを指定する数値
     * @return 色を量子化した画像。呼び出し元が [Mat.release] で解放する責任を持つ
     * @throws IllegalArgumentException [colorCount] が0以下の場合
     * @throws IllegalArgumentException [image] が空の場合
     * @throws IllegalArgumentException [colorCount] が元画像のピクセル数より大きい場合
     */
    fun quantizeColors(
        image: Mat,
        colorCount: Int,
    ): Mat {
        if (colorCount <= 0) throw IllegalArgumentException("The colorCount must be greater than zero.")

        if (image.empty()) throw IllegalArgumentException("The image must not be empty.")

        if (colorCount >
            image.rows() * image.cols()
        ) {
            throw IllegalArgumentException("colorCount exceeds the number of pixels.")
        }

        val floatPixels = Mat()
        val bestLabels = Mat()
        val centers = Mat()
        val centers8U = Mat()
        val result = Mat(image.rows() * image.cols(), 1, image.type())
        try {
            val pixels = image.reshape(1, image.rows() * image.cols())
            pixels.convertTo(floatPixels, CV_32F)

            val terminationConditions = TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 100, 0.1)
            kmeans(floatPixels, colorCount, bestLabels, terminationConditions, 3, KMEANS_PP_CENTERS, centers)

            centers.convertTo(centers8U, CV_8U)

            val resultIdx = result.createIndexer<UByteIndexer>()
            val labelsIdx = bestLabels.createIndexer<IntIndexer>()
            val centersIdx = centers8U.createIndexer<UByteIndexer>()
            for (i in 0 until image.rows() * image.cols()) {
                val label = labelsIdx.get(i.toLong())
                for (c in 0 until image.channels()) {
                    val color = centersIdx.get(label.toLong(), c.toLong())
                    resultIdx.put(i.toLong(), 0L, c.toLong(), color)
                }
            }
        } catch (e: Exception) {
            floatPixels.release()
            bestLabels.release()
            centers.release()
            centers8U.release()
            result.release()
            throw e
        }

        val dstImg = result.reshape(image.channels(), image.rows())

        return dstImg
    }
}

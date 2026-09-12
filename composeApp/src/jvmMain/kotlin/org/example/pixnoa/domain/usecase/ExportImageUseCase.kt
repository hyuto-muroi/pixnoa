package org.example.pixnoa.domain.usecase

import org.example.pixnoa.domain.repository.ImageRepository

/** 画像を書き出すユースケース */
class ExportImageUseCase(
    private val repository: ImageRepository,
) {
    /**
     * 指定したパスに画像を書き出す
     *
     * [savePath] の拡張子が `.png` でない場合は自動的に `.png` を付与する。
     *
     * @param imageBytes 書き出す変換後の画像のバイト列
     * @param savePath 出力先のパス
     */
    fun execute(
        imageBytes: ByteArray,
        savePath: String,
    ) {
        val normalizedPath = if (savePath.endsWith(".png", ignoreCase = true)) savePath else "$savePath.png"
        repository.save(imageBytes, normalizedPath)
    }
}

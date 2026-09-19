package org.example.pixnoa.domain.repository

import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult

/** 画像をドット絵に変換する処理の口を定義するインターフェース */
interface PixelArtConverter {
    /**
     * 画像をドット絵に変換する
     *
     * @param imageBytes 変換する元の画像のバイト列
     * @param config ドットサイズ・色数などの変換設定
     * @return 変換後の画像を表す [PixelArtResult]
     */
    fun convert(
        imageBytes: ByteArray,
        config: PixelArtConfig,
    ): PixelArtResult
}

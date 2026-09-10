package org.example.pixnoa.domain.usecase

import org.example.pixnoa.domain.model.PixelArtConfig
import org.example.pixnoa.domain.model.PixelArtResult
import org.example.pixnoa.domain.repository.PixelArtConverter

/** 画像をドット絵に変換するユースケース */
class ConvertToPixelArtUseCase(
    private val converter: PixelArtConverter,
) {
    /**
     * 画像をドット絵に変換する
     *
     * @param imageBytes 変換する元の画像のバイト列
     * @param config ドットサイズ・色数などの変換設定
     * @return 変換後の画像を表す [PixelArtResult]
     */
    fun execute(
        imageBytes: ByteArray,
        config: PixelArtConfig,
    ): PixelArtResult = converter.convert(imageBytes, config)
}

package org.example.pixnoa.domain.usecase

import org.example.pixnoa.domain.repository.ImageRepository

/** 画像を読み込むユースケース */
class LoadImageUseCase(
    private val imageRepository: ImageRepository,
) {
    /**
     * 指定したパスの画像を読み込む
     *
     * @param path 読み込む画像のパス
     * @return 読み込んだ画像のバイト列
     */
    fun execute(path: String): ByteArray = imageRepository.load(path)
}

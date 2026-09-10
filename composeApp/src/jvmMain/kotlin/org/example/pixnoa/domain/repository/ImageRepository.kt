package org.example.pixnoa.domain.repository

/** 画像の読み込み・書き出しを行う処理の口を定義するインターフェース */
interface ImageRepository {
    /**
     * 指定したパスの画像を読み込む
     *
     * @param path 読み込む画像のパス
     * @return 読み込んだ画像のバイト列
     */
    fun load(path: String): ByteArray

    /**
     * 画像を指定したパスに書き出す
     *
     * @param bytes 書き出す画像のバイト列
     * @param path 書き出し先のパス
     */
    fun save(
        bytes: ByteArray,
        path: String,
    )
}

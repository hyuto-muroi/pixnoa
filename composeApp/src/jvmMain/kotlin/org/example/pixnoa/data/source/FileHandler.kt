package org.example.pixnoa.data.source

import java.io.File

/** ファイルの読み書きとファイル選択ダイアログを提供するオブジェクト */
object FileHandler {
    /**
     * 指定パスのファイルをバイト列で読み込む
     *
     * @param path 読み込むファイルのパス
     * @return 読み込んだファイルの内容
     * @throws IllegalArgumentException [path] が空文字あるいは空白の場合
     * @throws java.io.IOException ファイルが読み込めない、または存在しない場合
     */
    fun readBytes(path: String): ByteArray {
        require(path.isNotBlank()) { "The path must not be blank." }
        return File(path).readBytes()
    }
}

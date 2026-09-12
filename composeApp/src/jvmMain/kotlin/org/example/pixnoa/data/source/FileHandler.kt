package org.example.pixnoa.data.source

import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane

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

    /**
     * 指定パスにファイルを出力する
     *
     * @param bytes 出力するファイルの内容
     * @param path ファイルの出力先
     * @throws IllegalArgumentException [bytes] が空データの場合
     * @throws IllegalArgumentException [path] が空文字あるいは空白の場合
     * @throws java.io.FileNotFoundException ファイルが存在しない、または書き出し先のディレクトリが存在しない場合
     */
    fun writeBytes(
        bytes: ByteArray,
        path: String,
    ) {
        require(bytes.isNotEmpty()) { "The bytes must not be empty." }
        require(path.isNotBlank()) { "The path must not be blank." }
        File(path).writeBytes(bytes)
    }

    /**
     * ファイル選択ダイアログを表示し、選択されたファイルのパスを取得する
     *
     * @param chooser 表示するファイル選択ダイアログ
     * @return 選択されたファイルのパス（ダイアログがキャンセルされた場合は null を返す）
     */
    fun openFileDialog(chooser: JFileChooser = JFileChooser()): String? {
        val result = chooser.showOpenDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile.path else null
    }

    /**
     * ファイル保存ダイアログを表示し、保存先のパスを取得する
     *
     * 選択したパス（`.png`拡張子がない場合は補ったパス）に同名のファイルが既に存在する場合、
     * [confirmOverwrite] で上書きするかどうかを確認する。上書きしない場合はダイアログを表示し直す。
     *
     * @param chooser 表示するファイル保存ダイアログ
     * @param confirmOverwrite 同名のファイルが既に存在する場合に、上書きするかどうかを確認する処理
     * @return 保存先のパス（ダイアログがキャンセルされた場合は null を返す）
     */
    fun saveFileDialog(
        chooser: JFileChooser = JFileChooser(),
        confirmOverwrite: (path: String) -> Boolean = ::showOverwriteConfirmDialog,
    ): String? {
        while (true) {
            val result = chooser.showSaveDialog(null)
            if (result != JFileChooser.APPROVE_OPTION) return null

            val path = chooser.selectedFile.path
            val normalizedPath = if (path.endsWith(".png", ignoreCase = true)) path else "$path.png"

            if (!File(normalizedPath).exists() || confirmOverwrite(normalizedPath)) return path
        }
    }

    private fun showOverwriteConfirmDialog(path: String): Boolean {
        val choice =
            JOptionPane.showConfirmDialog(
                null,
                "$path は既に存在します。上書きしますか？",
                "確認",
                JOptionPane.YES_NO_OPTION,
            )
        return choice == JOptionPane.YES_OPTION
    }
}

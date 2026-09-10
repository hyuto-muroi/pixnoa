package org.example.pixnoa.ui.screen.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.pixnoa.domain.usecase.ConvertToPixelArtUseCase
import org.example.pixnoa.domain.usecase.LoadImageUseCase

/** ドット絵変換画面の状態管理とユースケース呼び出しを行うViewModel */
class ConverterViewModel(
    private val loadImageUseCase: LoadImageUseCase,
    private val convertToPixelArtUseCase: ConvertToPixelArtUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConverterState())

    /** 画面の状態を表す [StateFlow] */
    val uiState: StateFlow<ConverterState> = _uiState.asStateFlow()

    /**
     * 画像が選択された時に呼び出す
     *
     * 画像の読み込みと変換をバックグラウンドで非同期に実行し、完了後に [uiState] を更新する。
     * 読み込みまたは変換に失敗した場合は例外を投げず、[ConverterState.error] にメッセージを格納する。
     *
     * @param path 選択された画像のパス
     */
    fun onImageSelected(path: String) {
        _uiState.update { it.copy(isConverting = true) }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val originalImage = loadImageUseCase.execute(path)
                val convertedImage = convertToPixelArtUseCase.execute(originalImage, _uiState.value.config)

                _uiState.update {
                    it.copy(
                        originalImage = originalImage,
                        convertedImage = convertedImage.imageBytes,
                        isConverting = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isConverting = false, error = e.toString()) }
            }
        }
    }

    fun onClear() {
        _uiState.update { ConverterState() }
    }
}

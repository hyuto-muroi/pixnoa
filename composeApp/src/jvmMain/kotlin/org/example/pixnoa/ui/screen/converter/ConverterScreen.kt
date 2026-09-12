package org.example.pixnoa.ui.screen.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.pixnoa.data.source.FileHandler
import org.example.pixnoa.domain.usecase.ConvertToPixelArtUseCase
import org.example.pixnoa.domain.usecase.LoadImageUseCase
import org.example.pixnoa.ui.component.imagePreview

@Composable
fun converterScreen(
    loadImageUseCase: LoadImageUseCase,
    convertToPixelArtUseCase: ConvertToPixelArtUseCase,
    viewModel: ConverterViewModel = viewModel { ConverterViewModel(loadImageUseCase, convertToPixelArtUseCase) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        imagePreview(
            imageBytes = state.convertedImage,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    val path = FileHandler.openFileDialog()
                    if (path != null) {
                        viewModel.onImageSelected(path)
                    }
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A84FF),
                        contentColor = Color.White,
                    ),
                shape = RectangleShape,
            ) { Text("ファイルを開く") }
            Button(
                onClick = { viewModel.onClear() },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A84FF),
                        contentColor = Color.White,
                    ),
                shape = RectangleShape,
            ) { Text("クリア") }
        }
    }
}

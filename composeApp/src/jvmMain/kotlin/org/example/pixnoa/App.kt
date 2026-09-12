package org.example.pixnoa

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.pixnoa.data.repository.ImageRepositoryImpl
import org.example.pixnoa.data.repository.PixelArtConverterImpl
import org.example.pixnoa.domain.usecase.ConvertToPixelArtUseCase
import org.example.pixnoa.domain.usecase.LoadImageUseCase
import org.example.pixnoa.ui.screen.converter.converterScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun app() {
    MaterialTheme {
        converterScreen(
            loadImageUseCase = LoadImageUseCase(ImageRepositoryImpl()),
            convertToPixelArtUseCase = ConvertToPixelArtUseCase(PixelArtConverterImpl()),
        )
    }
}

package org.example.pixnoa.ui.screen.converter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.example.pixnoa.data.source.FileHandler
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun converterScreen() {
    var imagePath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val imageBytes = imagePath?.let { imagePath -> FileHandler.readBytes(imagePath) }
    val imageBitmap =
        imageBytes?.let { bytes ->
            remember(imageBytes) {
                try {
                    errorMessage = null
                    SkiaImage.makeFromEncoded(imageBytes).toComposeImageBitmap()
                } catch (_: Exception) {
                    errorMessage = "画像を読み込めませんでした"
                    null
                }
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                imageBitmap != null -> {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "選択した画像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }

                errorMessage != null -> {
                    Text(errorMessage!!)
                }

                else -> {
                    Text("画像が選択されていません")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { imagePath = FileHandler.openFileDialog() },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A84FF),
                        contentColor = Color.White,
                    ),
                shape = RectangleShape,
            ) { Text("ファイルを開く") }
            Button(
                onClick = {
                    imagePath = null
                    errorMessage = null
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A84FF),
                        contentColor = Color.White,
                    ),
                shape = RectangleShape,
            ) { Text("閉じる") }
        }
    }
}

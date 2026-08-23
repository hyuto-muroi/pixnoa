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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.example.pixnoa.data.source.FileHandler
import org.example.pixnoa.ui.component.imagePreview

@Composable
fun converterScreen() {
    var imagePath by remember { mutableStateOf<String?>(null) }
    val imageBytes = imagePath?.let { path -> FileHandler.readBytes(path) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        imagePreview(
            imageBytes = imageBytes,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

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
                onClick = { imagePath = null },
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

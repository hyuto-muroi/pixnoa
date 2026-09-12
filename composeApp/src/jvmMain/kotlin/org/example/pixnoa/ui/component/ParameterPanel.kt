@file:OptIn(ExperimentalMaterial3Api::class)

package org.example.pixnoa.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.pixnoa.domain.model.PixelArtConfig
import kotlin.math.roundToInt

private val ACCENT_COLOR = Color(0xFF0A84FF)
private val TRACK_INACTIVE_COLOR = Color(0xFFD1D1D6)

@Composable
fun parameterPanel(
    config: PixelArtConfig,
    onDotSizeChange: (Int) -> Unit,
    onColorCountChange: (Int) -> Unit,
) {
    var dotSizeSliderValue by remember(config.dotSize) { mutableStateOf(config.dotSize.toFloat()) }
    var colorCountSliderValue by remember(config.colorCount) { mutableStateOf(config.colorCount.toFloat()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Slider(
            modifier = Modifier.width(200.dp),
            value = dotSizeSliderValue,
            onValueChange = { dotSizeSliderValue = it },
            onValueChangeFinished = { onDotSizeChange(dotSizeSliderValue.roundToInt()) },
            valueRange = 4f..32f,
            track = { macSliderTrack(it) },
            thumb = { macSliderThumb() },
        )
        Text(
            text = "ドットサイズ: ${dotSizeSliderValue.roundToInt()}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Slider(
            modifier = Modifier.width(200.dp),
            value = colorCountSliderValue,
            onValueChange = { colorCountSliderValue = it },
            onValueChangeFinished = { onColorCountChange(colorCountSliderValue.roundToInt()) },
            valueRange = 2f..256f,
            track = { macSliderTrack(it) },
            thumb = { macSliderThumb() },
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "色数: ${colorCountSliderValue.roundToInt()}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/** macOS風の細いトラック（塗りつぶし部分とそれ以外を色分けした線）を描画する */
@Composable
private fun macSliderTrack(sliderState: SliderState) {
    Row(modifier = Modifier.fillMaxWidth().height(4.dp)) {
        Box(
            modifier =
                Modifier
                    // weightは0以下だとクラッシュするため、両端でも最低限の比率を確保する
                    .weight(sliderState.coercedValueAsFraction.coerceAtLeast(0.0001f))
                    .fillMaxHeight()
                    .background(color = ACCENT_COLOR, shape = RoundedCornerShape(percent = 50)),
        )
        Box(
            modifier =
                Modifier
                    .weight((1f - sliderState.coercedValueAsFraction).coerceAtLeast(0.0001f))
                    .fillMaxHeight()
                    .background(color = TRACK_INACTIVE_COLOR, shape = RoundedCornerShape(percent = 50)),
        )
    }
}

/** macOS風の小さな円形のつまみを描画する */
@Composable
private fun macSliderThumb() {
    Box(
        modifier =
            Modifier
                .size(16.dp)
                .shadow(elevation = 1.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White),
    )
}

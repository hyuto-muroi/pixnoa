package org.example.pixnoa.ui.screen.converter

import org.example.pixnoa.domain.model.PixelArtConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ConverterStateTest {
    // 中身が同じByteArrayを持つ2つのConverterStateは等しいと判定されること
    @Test
    fun equals_statesWithSameContentByteArrays_areEqual() {
        val a = ConverterState(originalImage = byteArrayOf(1, 2, 3), convertedImage = byteArrayOf(4, 5, 6))
        val b = ConverterState(originalImage = byteArrayOf(1, 2, 3), convertedImage = byteArrayOf(4, 5, 6))

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // 中身が異なるByteArrayを持つ場合は等しくないと判定されること
    @Test
    fun equals_statesWithDifferentContentByteArrays_areNotEqual() {
        val a = ConverterState(originalImage = byteArrayOf(1, 2, 3))
        val b = ConverterState(originalImage = byteArrayOf(9, 9, 9))

        assertNotEquals(a, b)
    }

    // isConvertingだけが異なる場合は等しくないと判定されること
    @Test
    fun equals_statesDifferingOnlyInIsConverting_areNotEqual() {
        val a = ConverterState(isConverting = true)
        val b = ConverterState(isConverting = false)

        assertNotEquals(a, b)
    }

    // errorだけが異なる場合は等しくないと判定されること
    @Test
    fun equals_statesDifferingOnlyInError_areNotEqual() {
        val a = ConverterState(error = "error1")
        val b = ConverterState(error = "error2")

        assertNotEquals(a, b)
    }

    // configだけが異なる場合は等しくないと判定されること
    @Test
    fun equals_statesDifferingOnlyInConfig_areNotEqual() {
        val a = ConverterState(config = PixelArtConfig(dotSize = 4, colorCount = 4))
        val b = ConverterState(config = PixelArtConfig(dotSize = 8, colorCount = 16))

        assertNotEquals(a, b)
    }
}

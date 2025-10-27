package com.letsgotoperfection.editor.impl

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.letsgotoperfection.editor.impl.formatting.TextFormatter
import org.junit.Assert.*
import org.junit.Test

class TextFormatterTest {

    private val formatter = TextFormatter()

    @Test
    fun `applyBold adds bold style to selection`() {
        val text = AnnotatedString("Hello World")
        val result = formatter.applyBold(text, 0, 5)

        assertTrue(result.spanStyles.any {
            it.item.fontWeight == FontWeight.Bold &&
            it.start == 0 &&
            it.end == 5
        })
    }

    @Test
    fun `applyItalic adds italic style to selection`() {
        val text = AnnotatedString("Hello World")
        val result = formatter.applyItalic(text, 6, 11)

        assertTrue(result.spanStyles.any {
            it.item.fontStyle == FontStyle.Italic &&
            it.start == 6 &&
            it.end == 11
        })
    }

    @Test
    fun `applyTextColor adds color to selection`() {
        val text = AnnotatedString("Hello World")
        val result = formatter.applyTextColor(text, 0, 5, Color.Red)

        assertTrue(result.spanStyles.any {
            it.item.color == Color.Red &&
            it.start == 0 &&
            it.end == 5
        })
    }

    @Test
    fun `clearFormatting removes all formatting from selection`() {
        var text = AnnotatedString("Hello World")
        text = formatter.applyBold(text, 0, 5)
        text = formatter.applyItalic(text, 0, 5)

        val result = formatter.clearFormatting(text, 0, 5)

        val hasFormattingInRange = result.spanStyles.any {
            it.start < 5 && it.end > 0
        }
        assertFalse(hasFormattingInRange)
    }
}

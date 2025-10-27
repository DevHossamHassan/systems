package com.letsgotoperfection.editor.api.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * Text formatting styles that can be applied to text selections.
 */
data class TextStyle(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val code: Boolean = false,
    val textColor: Color? = null,
    val backgroundColor: Color? = null,
    val fontSize: Float? = null,
    val fontWeight: FontWeight? = null,
    val fontStyle: FontStyle? = null,
    val textDecoration: TextDecoration? = null,
    val linkUrl: String? = null
) {
    companion object {
        val Default = TextStyle()
    }
}

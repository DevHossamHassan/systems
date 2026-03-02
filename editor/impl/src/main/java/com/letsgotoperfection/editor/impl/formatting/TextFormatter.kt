package com.letsgotoperfection.editor.impl.formatting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

/**
 * Handles text formatting operations for the editor.
 */
class TextFormatter {

    /**
     * Apply bold formatting to a selection.
     */
    fun applyBold(
        text: AnnotatedString,
        start: Int,
        end: Int
    ): AnnotatedString {
        return applySpanStyle(
            text, start, end,
            SpanStyle(fontWeight = FontWeight.Bold)
        )
    }

    /**
     * Apply italic formatting to a selection.
     */
    fun applyItalic(
        text: AnnotatedString,
        start: Int,
        end: Int
    ): AnnotatedString {
        return applySpanStyle(
            text, start, end,
            SpanStyle(fontStyle = FontStyle.Italic)
        )
    }

    /**
     * Apply underline formatting to a selection.
     */
    fun applyUnderline(
        text: AnnotatedString,
        start: Int,
        end: Int
    ): AnnotatedString {
        return applySpanStyle(
            text, start, end,
            SpanStyle(textDecoration = TextDecoration.Underline)
        )
    }

    /**
     * Apply strikethrough formatting to a selection.
     */
    fun applyStrikethrough(
        text: AnnotatedString,
        start: Int,
        end: Int
    ): AnnotatedString {
        return applySpanStyle(
            text, start, end,
            SpanStyle(textDecoration = TextDecoration.LineThrough)
        )
    }

    /**
     * Apply text color to a selection.
     */
    fun applyTextColor(
        text: AnnotatedString,
        start: Int,
        end: Int,
        color: Color
    ): AnnotatedString {
        return applySpanStyle(
            text, start, end,
            SpanStyle(color = color)
        )
    }

    /**
     * Apply background color to a selection.
     */
    fun applyBackgroundColor(
        text: AnnotatedString,
        start: Int,
        end: Int,
        color: Color
    ): AnnotatedString {
        return applySpanStyle(
            text, start, end,
            SpanStyle(background = color)
        )
    }

    /**
     * Apply link annotation to a selection.
     */
    fun applyLink(
        text: AnnotatedString,
        start: Int,
        end: Int,
        url: String
    ): AnnotatedString {
        return AnnotatedString.Builder(text).apply {
            addStyle(
                SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                ),
                start, end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = url,
                start = start,
                end = end
            )
        }.toAnnotatedString()
    }

    /**
     * Apply a generic span style to a selection.
     */
    private fun applySpanStyle(
        text: AnnotatedString,
        start: Int,
        end: Int,
        style: SpanStyle
    ): AnnotatedString {
        return AnnotatedString.Builder(text).apply {
            addStyle(style, start, end)
        }.toAnnotatedString()
    }

    /**
     * Remove formatting from a selection.
     */
    fun clearFormatting(
        text: AnnotatedString,
        start: Int,
        end: Int
    ): AnnotatedString {
        val builder = AnnotatedString.Builder()

        // Add text before selection
        if (start > 0) {
            builder.append(text.subSequence(0, start))
        }

        // Add selection without formatting
        builder.append(text.text.substring(start, end))

        // Add text after selection
        if (end < text.length) {
            builder.append(text.subSequence(end, text.length))
        }

        return builder.toAnnotatedString()
    }

    /**
     * Toggle formatting at a selection.
     */
    fun toggleStyle(
        text: AnnotatedString,
        start: Int,
        end: Int,
        style: FormattingStyle
    ): AnnotatedString {
        val hasStyle = hasStyleInRange(text, start, end, style)

        return if (hasStyle) {
            removeStyle(text, start, end, style)
        } else {
            when (style) {
                FormattingStyle.BOLD -> applyBold(text, start, end)
                FormattingStyle.ITALIC -> applyItalic(text, start, end)
                FormattingStyle.UNDERLINE -> applyUnderline(text, start, end)
                FormattingStyle.STRIKETHROUGH -> applyStrikethrough(text, start, end)
            }
        }
    }

    /**
     * Check if a style exists in the given range.
     */
    private fun hasStyleInRange(
        text: AnnotatedString,
        start: Int,
        end: Int,
        style: FormattingStyle
    ): Boolean {
        val styles = text.spanStyles
        return styles.any { spanStyle ->
            spanStyle.start < end && spanStyle.end > start &&
                when (style) {
                    FormattingStyle.BOLD -> spanStyle.item.fontWeight == FontWeight.Bold
                    FormattingStyle.ITALIC -> spanStyle.item.fontStyle == FontStyle.Italic
                    FormattingStyle.UNDERLINE -> spanStyle.item.textDecoration == TextDecoration.Underline
                    FormattingStyle.STRIKETHROUGH -> spanStyle.item.textDecoration == TextDecoration.LineThrough
                }
        }
    }

    /**
     * Remove a specific style from a range.
     */
    private fun removeStyle(
        text: AnnotatedString,
        start: Int,
        end: Int,
        style: FormattingStyle
    ): AnnotatedString {
        val builder = AnnotatedString.Builder()
        builder.append(text.text)

        // Copy all styles except the one we're removing
        text.spanStyles.forEach { spanStyle ->
            val shouldKeep = when (style) {
                FormattingStyle.BOLD -> spanStyle.item.fontWeight != FontWeight.Bold
                FormattingStyle.ITALIC -> spanStyle.item.fontStyle != FontStyle.Italic
                FormattingStyle.UNDERLINE -> spanStyle.item.textDecoration != TextDecoration.Underline
                FormattingStyle.STRIKETHROUGH -> spanStyle.item.textDecoration != TextDecoration.LineThrough
            }

            if (shouldKeep || spanStyle.start >= end || spanStyle.end <= start) {
                builder.addStyle(spanStyle.item, spanStyle.start, spanStyle.end)
            }
        }

        // Copy annotations
        text.getStringAnnotations(0, text.length).forEach { annotation ->
            builder.addStringAnnotation(
                tag = annotation.tag,
                annotation = annotation.item,
                start = annotation.start,
                end = annotation.end
            )
        }

        return builder.toAnnotatedString()
    }
}

/**
 * Formatting styles supported by the editor.
 */
enum class FormattingStyle {
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH
}

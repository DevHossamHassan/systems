package com.letsgotoperfection.editor.api.model

/**
 * Defines paragraph-level styles for text blocks.
 */
sealed class TextBlockStyle {
    data object Body : TextBlockStyle()
    data object Heading1 : TextBlockStyle()
    data object Heading2 : TextBlockStyle()
    data object Heading3 : TextBlockStyle()
    data object Heading4 : TextBlockStyle()
    data object Heading5 : TextBlockStyle()
    data object Heading6 : TextBlockStyle()
    data object Quote : TextBlockStyle()
    data object OrderedList : TextBlockStyle()
    data object UnorderedList : TextBlockStyle()
    data class Custom(val name: String) : TextBlockStyle()
}

package com.letsgotoperfection.editor.api.export

/**
 * Supported export formats for documents.
 */
enum class ExportFormat {
    MARKDOWN,
    HTML,
    PDF,
    JSON,
    PLAIN_TEXT;

    val fileExtension: String
        get() = when (this) {
            MARKDOWN -> "md"
            HTML -> "html"
            PDF -> "pdf"
            JSON -> "json"
            PLAIN_TEXT -> "txt"
        }

    val mimeType: String
        get() = when (this) {
            MARKDOWN -> "text/markdown"
            HTML -> "text/html"
            PDF -> "application/pdf"
            JSON -> "application/json"
            PLAIN_TEXT -> "text/plain"
        }
}

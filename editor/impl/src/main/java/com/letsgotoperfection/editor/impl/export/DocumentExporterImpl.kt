package com.letsgotoperfection.editor.impl.export

import com.letsgotoperfection.editor.api.export.DocumentExporter
import com.letsgotoperfection.editor.api.export.ExportFormat
import com.letsgotoperfection.editor.api.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Implementation of DocumentExporter.
 */
class DocumentExporterImpl @Inject constructor(
    private val json: Json
) : DocumentExporter {

    override suspend fun export(document: EditorDocument, format: ExportFormat): Result<String> {
        return runCatching {
            when (format) {
                ExportFormat.MARKDOWN -> exportToMarkdown(document)
                ExportFormat.HTML -> exportToHtml(document)
                ExportFormat.JSON -> exportToJson(document)
                ExportFormat.PLAIN_TEXT -> exportToPlainText(document)
                ExportFormat.PDF -> throw UnsupportedOperationException("PDF export requires file destination")
            }
        }
    }

    override suspend fun exportToFile(
        document: EditorDocument,
        format: ExportFormat,
        destinationUri: String
    ): Result<String> {
        return runCatching {
            val content = export(document, format).getOrThrow()
            // File writing logic would go here
            destinationUri
        }
    }

    private fun exportToMarkdown(document: EditorDocument): String {
        val builder = StringBuilder()

        // Title
        if (document.title.isNotEmpty()) {
            builder.appendLine("# ${document.title}")
            builder.appendLine()
        }

        // Blocks
        document.blocks.forEach { block ->
            when (block) {
                is TextBlock -> {
                    val text = block.text.text
                    when (block.style) {
                        is TextBlockStyle.Heading1 -> builder.appendLine("# $text")
                        is TextBlockStyle.Heading2 -> builder.appendLine("## $text")
                        is TextBlockStyle.Heading3 -> builder.appendLine("### $text")
                        is TextBlockStyle.Heading4 -> builder.appendLine("#### $text")
                        is TextBlockStyle.Heading5 -> builder.appendLine("##### $text")
                        is TextBlockStyle.Heading6 -> builder.appendLine("###### $text")
                        is TextBlockStyle.Quote -> builder.appendLine("> $text")
                        is TextBlockStyle.UnorderedList -> builder.appendLine("- $text")
                        is TextBlockStyle.OrderedList -> builder.appendLine("1. $text")
                        else -> builder.appendLine(text)
                    }
                    builder.appendLine()
                }
                is MediaBlock -> {
                    when (block.mediaType) {
                        MediaType.IMAGE, MediaType.GIF -> {
                            val caption = block.caption ?: "Image"
                            builder.appendLine("![$caption](${block.uri})")
                        }
                        MediaType.VIDEO -> {
                            builder.appendLine("[Video](${block.uri})")
                        }
                        MediaType.AUDIO -> {
                            builder.appendLine("[Audio](${block.uri})")
                        }
                    }
                    builder.appendLine()
                }
                is ChecklistBlock -> {
                    block.title?.let {
                        builder.appendLine("**$it**")
                        builder.appendLine()
                    }
                    block.items.forEach { item ->
                        val checkbox = if (item.isChecked) "[x]" else "[ ]"
                        builder.appendLine("- $checkbox ${item.text.text}")
                    }
                    builder.appendLine()
                }
                is FileAttachmentBlock -> {
                    builder.appendLine("[${block.fileName}](${block.fileUri})")
                    builder.appendLine()
                }
                is DividerBlock -> {
                    builder.appendLine("---")
                    builder.appendLine()
                }
                is CodeBlock -> {
                    val lang = block.language ?: ""
                    builder.appendLine("```$lang")
                    builder.appendLine(block.code)
                    builder.appendLine("```")
                    builder.appendLine()
                }
            }
        }

        return builder.toString()
    }

    private fun exportToHtml(document: EditorDocument): String {
        val builder = StringBuilder()

        builder.appendLine("<!DOCTYPE html>")
        builder.appendLine("<html>")
        builder.appendLine("<head>")
        builder.appendLine("  <meta charset=\"UTF-8\">")
        builder.appendLine("  <title>${document.title}</title>")
        builder.appendLine("  <style>")
        builder.appendLine("    body { font-family: sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }")
        builder.appendLine("    img { max-width: 100%; height: auto; }")
        builder.appendLine("    code { background: #f4f4f4; padding: 2px 4px; border-radius: 3px; }")
        builder.appendLine("    pre { background: #f4f4f4; padding: 10px; border-radius: 5px; overflow-x: auto; }")
        builder.appendLine("  </style>")
        builder.appendLine("</head>")
        builder.appendLine("<body>")

        // Title
        if (document.title.isNotEmpty()) {
            builder.appendLine("  <h1>${escapeHtml(document.title)}</h1>")
        }

        // Blocks
        document.blocks.forEach { block ->
            when (block) {
                is TextBlock -> {
                    val text = escapeHtml(block.text.text)
                    val tag = when (block.style) {
                        is TextBlockStyle.Heading1 -> "h1"
                        is TextBlockStyle.Heading2 -> "h2"
                        is TextBlockStyle.Heading3 -> "h3"
                        is TextBlockStyle.Heading4 -> "h4"
                        is TextBlockStyle.Heading5 -> "h5"
                        is TextBlockStyle.Heading6 -> "h6"
                        is TextBlockStyle.Quote -> "blockquote"
                        else -> "p"
                    }
                    builder.appendLine("  <$tag>$text</$tag>")
                }
                is MediaBlock -> {
                    when (block.mediaType) {
                        MediaType.IMAGE, MediaType.GIF -> {
                            builder.appendLine("  <img src=\"${block.uri}\" alt=\"${block.caption ?: ""}\" />")
                        }
                        MediaType.VIDEO -> {
                            builder.appendLine("  <video src=\"${block.uri}\" controls></video>")
                        }
                        MediaType.AUDIO -> {
                            builder.appendLine("  <audio src=\"${block.uri}\" controls></audio>")
                        }
                    }
                }
                is ChecklistBlock -> {
                    builder.appendLine("  <ul style=\"list-style: none;\">")
                    block.items.forEach { item ->
                        val checked = if (item.isChecked) "checked" else ""
                        builder.appendLine("    <li><input type=\"checkbox\" $checked disabled> ${escapeHtml(item.text.text)}</li>")
                    }
                    builder.appendLine("  </ul>")
                }
                is DividerBlock -> {
                    builder.appendLine("  <hr />")
                }
                is CodeBlock -> {
                    builder.appendLine("  <pre><code>${escapeHtml(block.code)}</code></pre>")
                }
                else -> {}
            }
        }

        builder.appendLine("</body>")
        builder.appendLine("</html>")

        return builder.toString()
    }

    private fun exportToJson(document: EditorDocument): String {
        // Simple JSON representation
        return """
        {
          "id": "${document.id}",
          "title": "${document.title}",
          "createdAt": ${document.createdAt},
          "updatedAt": ${document.updatedAt},
          "blocks": ${document.blocks.size}
        }
        """.trimIndent()
    }

    private fun exportToPlainText(document: EditorDocument): String {
        val builder = StringBuilder()

        if (document.title.isNotEmpty()) {
            builder.appendLine(document.title)
            builder.appendLine("=".repeat(document.title.length))
            builder.appendLine()
        }

        document.blocks.forEach { block ->
            when (block) {
                is TextBlock -> {
                    builder.appendLine(block.text.text)
                    builder.appendLine()
                }
                is ChecklistBlock -> {
                    block.items.forEach { item ->
                        val checkbox = if (item.isChecked) "[✓]" else "[ ]"
                        builder.appendLine("$checkbox ${item.text.text}")
                    }
                    builder.appendLine()
                }
                is CodeBlock -> {
                    builder.appendLine(block.code)
                    builder.appendLine()
                }
                else -> {}
            }
        }

        return builder.toString()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}

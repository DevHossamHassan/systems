package com.letsgotoperfection.editor.api.model

import androidx.compose.ui.text.AnnotatedString
import java.util.UUID

/**
 * Base sealed class for all content blocks in the editor.
 * Each block represents a discrete piece of content with a unique ID.
 */
sealed class ContentBlock {
    abstract val id: String
    abstract val position: Int
    abstract val createdAt: Long
    abstract val updatedAt: Long

    companion object {
        fun generateId(): String = UUID.randomUUID().toString()
    }
}

/**
 * Text block containing rich formatted text.
 */
data class TextBlock(
    override val id: String = generateId(),
    override val position: Int = 0,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    val text: AnnotatedString = AnnotatedString(""),
    val style: TextBlockStyle = TextBlockStyle.Body
) : ContentBlock()

/**
 * Media block for images, videos, and audio.
 */
data class MediaBlock(
    override val id: String = generateId(),
    override val position: Int = 0,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    val mediaType: MediaType,
    val uri: String,
    val metadata: MediaMetadata,
    val thumbnailUri: String? = null,
    val caption: String? = null
) : ContentBlock()

/**
 * Checklist block containing interactive todo items.
 */
data class ChecklistBlock(
    override val id: String = generateId(),
    override val position: Int = 0,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    val items: List<ChecklistItem> = emptyList(),
    val title: String? = null
) : ContentBlock()

/**
 * File attachment block for PDFs and other documents.
 */
data class FileAttachmentBlock(
    override val id: String = generateId(),
    override val position: Int = 0,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    val fileName: String,
    val fileUri: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailUri: String? = null
) : ContentBlock()

/**
 * Divider block for visual separation.
 */
data class DividerBlock(
    override val id: String = generateId(),
    override val position: Int = 0,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    val style: DividerStyle = DividerStyle.SOLID
) : ContentBlock()

/**
 * Code block for syntax-highlighted code.
 */
data class CodeBlock(
    override val id: String = generateId(),
    override val position: Int = 0,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis(),
    val code: String,
    val language: String? = null
) : ContentBlock()

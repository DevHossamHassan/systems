package com.letsgotoperfection.editor.impl.data.mappers

import kotlinx.serialization.Serializable

@Serializable
data class TextBlockJson(
    val text: String,
    val style: String
)

@Serializable
data class MediaBlockJson(
    val mediaType: String,
    val uri: String,
    val metadata: MetadataJson,
    val thumbnailUri: String? = null,
    val caption: String? = null
)

@Serializable
data class MetadataJson(
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null,
    val fileSize: Long,
    val mimeType: String,
    val originalFileName: String? = null
)

@Serializable
data class ChecklistBlockJson(
    val items: List<ChecklistItemJson>,
    val title: String? = null
)

@Serializable
data class ChecklistItemJson(
    val id: String,
    val text: String,
    val isChecked: Boolean,
    val createdAt: Long,
    val completedAt: Long? = null,
    val position: Int
)

@Serializable
data class FileAttachmentBlockJson(
    val fileName: String,
    val fileUri: String,
    val fileSize: Long,
    val mimeType: String,
    val thumbnailUri: String? = null
)

@Serializable
data class DividerBlockJson(
    val style: String
)

@Serializable
data class CodeBlockJson(
    val code: String,
    val language: String? = null
)

@Serializable
data class DocumentMetadataJson(
    val tags: List<String> = emptyList(),
    val favorite: Boolean = false,
    val archived: Boolean = false,
    val pinned: Boolean = false,
    val color: String? = null,
    val coverImageUri: String? = null,
    val customProperties: Map<String, String> = emptyMap()
)

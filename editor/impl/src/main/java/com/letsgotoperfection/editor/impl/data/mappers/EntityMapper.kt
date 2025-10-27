package com.letsgotoperfection.editor.impl.data.mappers

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.editor.api.model.*
import com.letsgotoperfection.editor.impl.data.database.ContentBlockEntity
import com.letsgotoperfection.editor.impl.data.database.DocumentEntity
import com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    prettyPrint = false
}

/**
 * Convert DocumentEntity to EditorDocument
 */
fun DocumentWithBlocks.toEditorDocument(): EditorDocument {
    val metadata = json.decodeFromString<DocumentMetadataJson>(document.metadata)
    return EditorDocument(
        id = document.id,
        title = document.title,
        blocks = blocks.map { it.toContentBlock() }.sortedBy { it.position },
        metadata = metadata.toDocumentMetadata(),
        createdAt = document.createdAt,
        updatedAt = document.updatedAt
    )
}

/**
 * Convert EditorDocument to DocumentEntity
 */
fun EditorDocument.toDocumentEntity(): DocumentEntity {
    val metadataJson = metadata.toJson()
    return DocumentEntity(
        id = id,
        title = title,
        metadata = json.encodeToString(metadataJson),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert EditorDocument to list of ContentBlockEntity
 */
fun EditorDocument.toBlockEntities(): List<ContentBlockEntity> {
    return blocks.mapIndexed { index, block ->
        block.toEntity(documentId = id, position = index)
    }
}

/**
 * Convert ContentBlock to ContentBlockEntity
 */
fun ContentBlock.toEntity(documentId: String, position: Int): ContentBlockEntity {
    val (type, content) = when (this) {
        is TextBlock -> "TEXT" to json.encodeToString(TextBlockJson(
            text = text.text,
            style = style.name()
        ))
        is MediaBlock -> "MEDIA" to json.encodeToString(MediaBlockJson(
            mediaType = mediaType.name,
            uri = uri,
            metadata = MetadataJson(
                width = metadata.width,
                height = metadata.height,
                duration = metadata.duration,
                fileSize = metadata.fileSize,
                mimeType = metadata.mimeType,
                originalFileName = metadata.originalFileName
            ),
            thumbnailUri = thumbnailUri,
            caption = caption
        ))
        is ChecklistBlock -> "CHECKLIST" to json.encodeToString(ChecklistBlockJson(
            items = items.map { ChecklistItemJson(
                id = it.id,
                text = it.text.text,
                isChecked = it.isChecked,
                createdAt = it.createdAt,
                completedAt = it.completedAt,
                position = it.position
            ) },
            title = title
        ))
        is FileAttachmentBlock -> "FILE_ATTACHMENT" to json.encodeToString(FileAttachmentBlockJson(
            fileName = fileName,
            fileUri = fileUri,
            fileSize = fileSize,
            mimeType = mimeType,
            thumbnailUri = thumbnailUri
        ))
        is DividerBlock -> "DIVIDER" to json.encodeToString(DividerBlockJson(
            style = style.name
        ))
        is CodeBlock -> "CODE" to json.encodeToString(CodeBlockJson(
            code = code,
            language = language
        ))
    }

    return ContentBlockEntity(
        id = id,
        documentId = documentId,
        blockType = type,
        content = content,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert ContentBlockEntity to ContentBlock
 */
fun ContentBlockEntity.toContentBlock(): ContentBlock {
    return when (blockType) {
        "TEXT" -> {
            val data = json.decodeFromString<TextBlockJson>(content)
            TextBlock(
                id = id,
                position = position,
                createdAt = createdAt,
                updatedAt = updatedAt,
                text = AnnotatedString(data.text),
                style = data.style.toTextBlockStyle()
            )
        }
        "MEDIA" -> {
            val data = json.decodeFromString<MediaBlockJson>(content)
            MediaBlock(
                id = id,
                position = position,
                createdAt = createdAt,
                updatedAt = updatedAt,
                mediaType = MediaType.valueOf(data.mediaType),
                uri = data.uri,
                metadata = MediaMetadata(
                    width = data.metadata.width,
                    height = data.metadata.height,
                    duration = data.metadata.duration,
                    fileSize = data.metadata.fileSize,
                    mimeType = data.metadata.mimeType,
                    originalFileName = data.metadata.originalFileName
                ),
                thumbnailUri = data.thumbnailUri,
                caption = data.caption
            )
        }
        "CHECKLIST" -> {
            val data = json.decodeFromString<ChecklistBlockJson>(content)
            ChecklistBlock(
                id = id,
                position = position,
                createdAt = createdAt,
                updatedAt = updatedAt,
                items = data.items.map {
                    ChecklistItem(
                        id = it.id,
                        text = AnnotatedString(it.text),
                        isChecked = it.isChecked,
                        createdAt = it.createdAt,
                        completedAt = it.completedAt,
                        position = it.position
                    )
                },
                title = data.title
            )
        }
        "FILE_ATTACHMENT" -> {
            val data = json.decodeFromString<FileAttachmentBlockJson>(content)
            FileAttachmentBlock(
                id = id,
                position = position,
                createdAt = createdAt,
                updatedAt = updatedAt,
                fileName = data.fileName,
                fileUri = data.fileUri,
                fileSize = data.fileSize,
                mimeType = data.mimeType,
                thumbnailUri = data.thumbnailUri
            )
        }
        "DIVIDER" -> {
            val data = json.decodeFromString<DividerBlockJson>(content)
            DividerBlock(
                id = id,
                position = position,
                createdAt = createdAt,
                updatedAt = updatedAt,
                style = DividerStyle.valueOf(data.style)
            )
        }
        "CODE" -> {
            val data = json.decodeFromString<CodeBlockJson>(content)
            CodeBlock(
                id = id,
                position = position,
                createdAt = createdAt,
                updatedAt = updatedAt,
                code = data.code,
                language = data.language
            )
        }
        else -> throw IllegalArgumentException("Unknown block type: $blockType")
    }
}

// Helper functions
private fun TextBlockStyle.name(): String = when (this) {
    is TextBlockStyle.Body -> "Body"
    is TextBlockStyle.Heading1 -> "Heading1"
    is TextBlockStyle.Heading2 -> "Heading2"
    is TextBlockStyle.Heading3 -> "Heading3"
    is TextBlockStyle.Heading4 -> "Heading4"
    is TextBlockStyle.Heading5 -> "Heading5"
    is TextBlockStyle.Heading6 -> "Heading6"
    is TextBlockStyle.Quote -> "Quote"
    is TextBlockStyle.OrderedList -> "OrderedList"
    is TextBlockStyle.UnorderedList -> "UnorderedList"
    is TextBlockStyle.Custom -> "Custom_${this.name}"
}

private fun String.toTextBlockStyle(): TextBlockStyle = when (this) {
    "Body" -> TextBlockStyle.Body
    "Heading1" -> TextBlockStyle.Heading1
    "Heading2" -> TextBlockStyle.Heading2
    "Heading3" -> TextBlockStyle.Heading3
    "Heading4" -> TextBlockStyle.Heading4
    "Heading5" -> TextBlockStyle.Heading5
    "Heading6" -> TextBlockStyle.Heading6
    "Quote" -> TextBlockStyle.Quote
    "OrderedList" -> TextBlockStyle.OrderedList
    "UnorderedList" -> TextBlockStyle.UnorderedList
    else -> if (startsWith("Custom_")) {
        TextBlockStyle.Custom(removePrefix("Custom_"))
    } else {
        TextBlockStyle.Body
    }
}

private fun DocumentMetadata.toJson(): DocumentMetadataJson = DocumentMetadataJson(
    tags = tags,
    favorite = favorite,
    archived = archived,
    pinned = pinned,
    color = color,
    coverImageUri = coverImageUri,
    customProperties = customProperties
)

private fun DocumentMetadataJson.toDocumentMetadata(): DocumentMetadata = DocumentMetadata(
    tags = tags,
    favorite = favorite,
    archived = archived,
    pinned = pinned,
    color = color,
    coverImageUri = coverImageUri,
    customProperties = customProperties
)

package com.letsgotoperfection.editor.api.model

import java.util.UUID

/**
 * Top-level document containing all content blocks and metadata.
 * This is the main entity that represents a document in the editor.
 */
data class EditorDocument(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val blocks: List<ContentBlock> = emptyList(),
    val metadata: DocumentMetadata = DocumentMetadata(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns a new document with blocks sorted by position.
     */
    fun sortBlocks(): EditorDocument {
        return copy(blocks = blocks.sortedBy { it.position })
    }

    /**
     * Returns a new document with the specified block added.
     */
    fun addBlock(block: ContentBlock): EditorDocument {
        val newBlocks = blocks + block
        return copy(
            blocks = newBlocks,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Returns a new document with the specified block updated.
     */
    fun updateBlock(blockId: String, updater: (ContentBlock) -> ContentBlock): EditorDocument {
        val newBlocks = blocks.map { if (it.id == blockId) updater(it) else it }
        return copy(
            blocks = newBlocks,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Returns a new document with the specified block removed.
     */
    fun removeBlock(blockId: String): EditorDocument {
        val newBlocks = blocks.filterNot { it.id == blockId }
        return copy(
            blocks = newBlocks,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Returns the total word count across all text blocks.
     */
    fun wordCount(): Int {
        return blocks.filterIsInstance<TextBlock>().sumOf {
            it.text.text.split(Regex("\\s+")).filter { word -> word.isNotBlank() }.size
        }
    }

    /**
     * Returns the total character count across all text blocks.
     */
    fun characterCount(): Int {
        return blocks.filterIsInstance<TextBlock>().sumOf { it.text.text.length }
    }
}

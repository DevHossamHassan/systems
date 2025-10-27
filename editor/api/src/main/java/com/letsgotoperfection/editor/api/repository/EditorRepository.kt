package com.letsgotoperfection.editor.api.repository

import com.letsgotoperfection.editor.api.model.ContentBlock
import com.letsgotoperfection.editor.api.model.EditorDocument
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for editor document operations.
 * Implementations should handle data persistence and retrieval.
 */
interface EditorRepository {

    /**
     * Observe a document by ID. Emits updates when the document changes.
     */
    fun observeDocument(documentId: String): Flow<EditorDocument?>

    /**
     * Observe all documents. Emits updates when documents are added, updated, or removed.
     */
    fun observeAllDocuments(): Flow<List<EditorDocument>>

    /**
     * Get a document by ID.
     */
    suspend fun getDocument(documentId: String): EditorDocument?

    /**
     * Get all documents.
     */
    suspend fun getAllDocuments(): List<EditorDocument>

    /**
     * Create a new document.
     */
    suspend fun createDocument(document: EditorDocument): Result<EditorDocument>

    /**
     * Update an existing document.
     */
    suspend fun updateDocument(document: EditorDocument): Result<EditorDocument>

    /**
     * Delete a document by ID.
     */
    suspend fun deleteDocument(documentId: String): Result<Unit>

    /**
     * Add a block to a document.
     */
    suspend fun addBlock(documentId: String, block: ContentBlock): Result<ContentBlock>

    /**
     * Update a block in a document.
     */
    suspend fun updateBlock(documentId: String, block: ContentBlock): Result<ContentBlock>

    /**
     * Delete a block from a document.
     */
    suspend fun deleteBlock(documentId: String, blockId: String): Result<Unit>

    /**
     * Reorder blocks in a document.
     */
    suspend fun reorderBlocks(documentId: String, blockIds: List<String>): Result<Unit>

    /**
     * Search documents by query.
     */
    suspend fun searchDocuments(query: String): List<EditorDocument>

    /**
     * Get documents by tag.
     */
    suspend fun getDocumentsByTag(tag: String): List<EditorDocument>

    /**
     * Get favorited documents.
     */
    suspend fun getFavoriteDocuments(): List<EditorDocument>
}

package com.letsgotoperfection.editor.api.repository

import com.letsgotoperfection.editor.api.model.EditorDocument
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for editor document operations.
 * Implementations should handle data persistence and retrieval.
 * All suspend functions should use appropriate dispatchers for background operations.
 */
interface EditorRepository {

    /**
     * Create a new document.
     */
    suspend fun createDocument(document: EditorDocument): Result<Unit>

    /**
     * Get a document by ID. Returns null if not found.
     */
    suspend fun getDocument(id: String): EditorDocument?

    /**
     * Update an existing document.
     */
    suspend fun updateDocument(document: EditorDocument): Result<Unit>

    /**
     * Delete a document by ID.
     */
    suspend fun deleteDocument(id: String): Result<Unit>

    /**
     * Get all documents as a Flow. Emits updates when documents change.
     */
    fun getAllDocuments(): Flow<List<EditorDocument>>

    /**
     * Observe a specific document. Emits updates when the document changes.
     */
    fun observeDocument(id: String): Flow<EditorDocument?>

    /**
     * Search documents by query string (searches title and content).
     */
    suspend fun searchDocuments(query: String): List<EditorDocument>
}

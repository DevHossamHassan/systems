package com.letsgotoperfection.editor.impl.fake

import com.letsgotoperfection.editor.api.model.EditorDocument
import com.letsgotoperfection.editor.api.repository.EditorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of EditorRepository for testing.
 * Follows Google's best practices for testing by using fakes instead of mocks.
 */
class FakeEditorRepository : EditorRepository {

    private val documents = mutableMapOf<String, EditorDocument>()
    private val documentsFlow = MutableStateFlow<Map<String, EditorDocument>>(emptyMap())

    var shouldReturnError = false
    var errorMessage = "Test error"

    override suspend fun createDocument(document: EditorDocument): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception(errorMessage))
        } else {
            documents[document.id] = document
            documentsFlow.value = documents.toMap()
            Result.success(Unit)
        }
    }

    override suspend fun getDocument(id: String): EditorDocument? {
        return documents[id]
    }

    override suspend fun updateDocument(document: EditorDocument): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception(errorMessage))
        } else {
            documents[document.id] = document
            documentsFlow.value = documents.toMap()
            Result.success(Unit)
        }
    }

    override suspend fun deleteDocument(id: String): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception(errorMessage))
        } else {
            documents.remove(id)
            documentsFlow.value = documents.toMap()
            Result.success(Unit)
        }
    }

    override fun getAllDocuments(): Flow<List<EditorDocument>> {
        return documentsFlow.map { it.values.toList() }
    }

    override fun observeDocument(id: String): Flow<EditorDocument?> {
        return documentsFlow.map { it[id] }
    }

    override suspend fun searchDocuments(query: String): List<EditorDocument> {
        return documents.values.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }

    // Test helpers
    fun getDocumentCount(): Int = documents.size

    fun clearAllDocuments() {
        documents.clear()
        documentsFlow.value = emptyMap()
    }

    fun hasDocument(id: String): Boolean = documents.containsKey(id)
}

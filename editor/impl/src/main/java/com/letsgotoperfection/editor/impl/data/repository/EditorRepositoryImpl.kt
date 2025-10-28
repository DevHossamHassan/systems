package com.letsgotoperfection.editor.impl.data.repository

import com.letsgotoperfection.editor.api.model.EditorDocument
import com.letsgotoperfection.editor.api.repository.EditorRepository
import com.letsgotoperfection.editor.impl.data.database.DocumentDao
import com.letsgotoperfection.editor.impl.data.mappers.toBlockEntities
import com.letsgotoperfection.editor.impl.data.mappers.toDocumentEntity
import com.letsgotoperfection.editor.impl.data.mappers.toEditorDocument
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of EditorRepository using Room database with proper coroutine dispatchers.
 * All database operations are performed on IO dispatcher following best practices.
 */
class EditorRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : EditorRepository {

    override suspend fun createDocument(document: EditorDocument): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val entity = document.toDocumentEntity()
                val blockEntities = document.toBlockEntities()
                documentDao.insertDocumentWithBlocks(entity, blockEntities)
            }
        }

    override suspend fun getDocument(id: String): EditorDocument? =
        withContext(ioDispatcher) {
            documentDao.getDocumentWithBlocks(id)?.toEditorDocument()
        }

    override suspend fun updateDocument(document: EditorDocument): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val entity = document.toDocumentEntity()
                documentDao.updateDocument(entity)

                // Update blocks - delete and re-insert to handle all changes
                documentDao.deleteAllBlocks(document.id)
                val blockEntities = document.toBlockEntities()
                documentDao.insertBlocks(blockEntities)
            }
        }

    override suspend fun deleteDocument(id: String): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                documentDao.deleteDocument(id)
            }
        }

    override fun getAllDocuments(): Flow<List<EditorDocument>> {
        return documentDao.observeAllDocuments().map { entities ->
            entities.map { entity ->
                val blocks = documentDao.getBlocks(entity.id)
                com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks(
                    document = entity,
                    blocks = blocks
                ).toEditorDocument()
            }
        }
    }

    override fun observeDocument(id: String): Flow<EditorDocument?> {
        return documentDao.observeDocument(id).map { entity ->
            entity?.let {
                val blocks = documentDao.getBlocks(id)
                com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks(
                    document = it,
                    blocks = blocks
                ).toEditorDocument()
            }
        }
    }

    override suspend fun searchDocuments(query: String): List<EditorDocument> =
        withContext(ioDispatcher) {
            documentDao.searchDocuments(query).map { entity ->
                val blocks = documentDao.getBlocks(entity.id)
                com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks(
                    document = entity,
                    blocks = blocks
                ).toEditorDocument()
            }
        }
}

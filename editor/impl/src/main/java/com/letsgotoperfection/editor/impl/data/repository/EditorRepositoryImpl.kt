package com.letsgotoperfection.editor.impl.data.repository

import com.letsgotoperfection.editor.api.model.ContentBlock
import com.letsgotoperfection.editor.api.model.EditorDocument
import com.letsgotoperfection.editor.api.repository.EditorRepository
import com.letsgotoperfection.editor.impl.data.database.DocumentDao
import com.letsgotoperfection.editor.impl.data.mappers.toBlockEntities
import com.letsgotoperfection.editor.impl.data.mappers.toDocumentEntity
import com.letsgotoperfection.editor.impl.data.mappers.toEditorDocument
import com.letsgotoperfection.editor.impl.data.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of EditorRepository using Room database.
 */
class EditorRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao
) : EditorRepository {

    override fun observeDocument(documentId: String): Flow<EditorDocument?> {
        return documentDao.observeDocument(documentId).map { entity ->
            entity?.let {
                val blocks = documentDao.getBlocks(documentId)
                com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks(
                    document = it,
                    blocks = blocks
                ).toEditorDocument()
            }
        }
    }

    override fun observeAllDocuments(): Flow<List<EditorDocument>> {
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

    override suspend fun getDocument(documentId: String): EditorDocument? {
        return documentDao.getDocumentWithBlocks(documentId)?.toEditorDocument()
    }

    override suspend fun getAllDocuments(): List<EditorDocument> {
        return documentDao.getAllDocuments().map { entity ->
            val blocks = documentDao.getBlocks(entity.id)
            com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks(
                document = entity,
                blocks = blocks
            ).toEditorDocument()
        }
    }

    override suspend fun createDocument(document: EditorDocument): Result<EditorDocument> {
        return runCatching {
            val entity = document.toDocumentEntity()
            val blockEntities = document.toBlockEntities()
            documentDao.insertDocumentWithBlocks(entity, blockEntities)
            document
        }
    }

    override suspend fun updateDocument(document: EditorDocument): Result<EditorDocument> {
        return runCatching {
            val entity = document.toDocumentEntity()
            documentDao.updateDocument(entity)

            // Update blocks
            documentDao.deleteAllBlocks(document.id)
            val blockEntities = document.toBlockEntities()
            documentDao.insertBlocks(blockEntities)

            document
        }
    }

    override suspend fun deleteDocument(documentId: String): Result<Unit> {
        return runCatching {
            documentDao.deleteDocument(documentId)
        }
    }

    override suspend fun addBlock(documentId: String, block: ContentBlock): Result<ContentBlock> {
        return runCatching {
            val document = getDocument(documentId)
                ?: throw IllegalArgumentException("Document not found: $documentId")

            val blockEntity = block.toEntity(documentId, block.position)
            documentDao.insertBlock(blockEntity)

            // Update document's updatedAt timestamp
            val updatedDocument = document.copy(updatedAt = System.currentTimeMillis())
            documentDao.updateDocument(updatedDocument.toDocumentEntity())

            block
        }
    }

    override suspend fun updateBlock(documentId: String, block: ContentBlock): Result<ContentBlock> {
        return runCatching {
            val blockEntity = block.toEntity(documentId, block.position)
            documentDao.updateBlock(blockEntity)

            // Update document's updatedAt timestamp
            val document = getDocument(documentId)
                ?: throw IllegalArgumentException("Document not found: $documentId")
            val updatedDocument = document.copy(updatedAt = System.currentTimeMillis())
            documentDao.updateDocument(updatedDocument.toDocumentEntity())

            block
        }
    }

    override suspend fun deleteBlock(documentId: String, blockId: String): Result<Unit> {
        return runCatching {
            documentDao.deleteBlock(blockId)

            // Update document's updatedAt timestamp
            val document = getDocument(documentId)
                ?: throw IllegalArgumentException("Document not found: $documentId")
            val updatedDocument = document.copy(updatedAt = System.currentTimeMillis())
            documentDao.updateDocument(updatedDocument.toDocumentEntity())
        }
    }

    override suspend fun reorderBlocks(documentId: String, blockIds: List<String>): Result<Unit> {
        return runCatching {
            blockIds.forEachIndexed { index, blockId ->
                val block = documentDao.getBlock(blockId)
                    ?: throw IllegalArgumentException("Block not found: $blockId")
                val updatedBlock = block.copy(position = index)
                documentDao.updateBlock(updatedBlock)
            }

            // Update document's updatedAt timestamp
            val document = getDocument(documentId)
                ?: throw IllegalArgumentException("Document not found: $documentId")
            val updatedDocument = document.copy(updatedAt = System.currentTimeMillis())
            documentDao.updateDocument(updatedDocument.toDocumentEntity())
        }
    }

    override suspend fun searchDocuments(query: String): List<EditorDocument> {
        return documentDao.searchDocuments(query).map { entity ->
            val blocks = documentDao.getBlocks(entity.id)
            com.letsgotoperfection.editor.impl.data.database.DocumentWithBlocks(
                document = entity,
                blocks = blocks
            ).toEditorDocument()
        }
    }

    override suspend fun getDocumentsByTag(tag: String): List<EditorDocument> {
        // This would require a more sophisticated query
        // For now, we'll filter in memory
        return getAllDocuments().filter { doc ->
            doc.metadata.tags.contains(tag)
        }
    }

    override suspend fun getFavoriteDocuments(): List<EditorDocument> {
        return getAllDocuments().filter { it.metadata.favorite }
    }
}

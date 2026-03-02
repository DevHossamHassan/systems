package com.letsgotoperfection.editor.impl.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for document operations.
 */
@Dao
interface DocumentDao {

    // Document operations
    @Query("SELECT * FROM documents WHERE id = :documentId")
    fun observeDocument(documentId: String): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    fun observeAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :documentId")
    suspend fun getDocument(documentId: String): DocumentEntity?

    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    suspend fun getAllDocuments(): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :documentId")
    suspend fun deleteDocument(documentId: String)

    @Query("SELECT * FROM documents WHERE title LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    suspend fun searchDocuments(query: String): List<DocumentEntity>

    // Content block operations
    @Query("SELECT * FROM content_blocks WHERE documentId = :documentId ORDER BY position ASC")
    fun observeBlocks(documentId: String): Flow<List<ContentBlockEntity>>

    @Query("SELECT * FROM content_blocks WHERE documentId = :documentId ORDER BY position ASC")
    suspend fun getBlocks(documentId: String): List<ContentBlockEntity>

    @Query("SELECT * FROM content_blocks WHERE id = :blockId")
    suspend fun getBlock(blockId: String): ContentBlockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: ContentBlockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<ContentBlockEntity>)

    @Update
    suspend fun updateBlock(block: ContentBlockEntity)

    @Query("DELETE FROM content_blocks WHERE id = :blockId")
    suspend fun deleteBlock(blockId: String)

    @Query("DELETE FROM content_blocks WHERE documentId = :documentId")
    suspend fun deleteAllBlocks(documentId: String)

    @Transaction
    suspend fun insertDocumentWithBlocks(document: DocumentEntity, blocks: List<ContentBlockEntity>) {
        insertDocument(document)
        insertBlocks(blocks)
    }

    @Transaction
    @Query("SELECT * FROM documents WHERE id = :documentId")
    suspend fun getDocumentWithBlocks(documentId: String): DocumentWithBlocks?
}

/**
 * Document with its content blocks.
 */
data class DocumentWithBlocks(
    @Embedded val document: DocumentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "documentId"
    )
    val blocks: List<ContentBlockEntity>
)

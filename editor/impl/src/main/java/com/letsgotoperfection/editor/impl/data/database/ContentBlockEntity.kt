package com.letsgotoperfection.editor.impl.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing content blocks.
 */
@Entity(
    tableName = "content_blocks",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("documentId"), Index("position")]
)
data class ContentBlockEntity(
    @PrimaryKey
    val id: String,
    val documentId: String,
    val blockType: String, // TEXT, MEDIA, CHECKLIST, FILE_ATTACHMENT, DIVIDER, CODE
    val content: String, // JSON string of the block data
    val position: Int,
    val createdAt: Long,
    val updatedAt: Long
)

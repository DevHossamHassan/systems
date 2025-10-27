package com.letsgotoperfection.editor.impl.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.letsgotoperfection.editor.impl.data.converters.DocumentMetadataConverter

/**
 * Room entity for storing documents.
 */
@Entity(tableName = "documents")
@TypeConverters(DocumentMetadataConverter::class)
data class DocumentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val metadata: String, // JSON string
    val createdAt: Long,
    val updatedAt: Long
)

package com.letsgotoperfection.editor.impl.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.letsgotoperfection.editor.impl.data.converters.DocumentMetadataConverter

/**
 * Room database for the editor.
 */
@Database(
    entities = [
        DocumentEntity::class,
        ContentBlockEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DocumentMetadataConverter::class)
abstract class EditorDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    companion object {
        const val DATABASE_NAME = "editor_database"
    }
}

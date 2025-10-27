package com.letsgotoperfection.editor.impl.di

import android.content.Context
import androidx.room.Room
import com.letsgotoperfection.editor.api.repository.EditorRepository
import com.letsgotoperfection.editor.impl.data.database.DocumentDao
import com.letsgotoperfection.editor.impl.data.database.EditorDatabase
import com.letsgotoperfection.editor.impl.data.repository.EditorRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing editor dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object EditorModule {

    @Provides
    @Singleton
    fun provideEditorDatabase(
        @ApplicationContext context: Context
    ): EditorDatabase {
        return Room.databaseBuilder(
            context,
            EditorDatabase::class.java,
            EditorDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For development; use proper migrations in production
            .build()
    }

    @Provides
    @Singleton
    fun provideDocumentDao(database: EditorDatabase): DocumentDao {
        return database.documentDao()
    }

    @Provides
    @Singleton
    fun provideEditorRepository(
        documentDao: DocumentDao
    ): EditorRepository {
        return EditorRepositoryImpl(documentDao)
    }
}

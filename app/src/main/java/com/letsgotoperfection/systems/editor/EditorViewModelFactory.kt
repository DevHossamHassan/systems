package com.letsgotoperfection.systems.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.letsgotoperfection.editor.impl.data.database.EditorDatabase
import com.letsgotoperfection.editor.impl.data.repository.EditorRepositoryImpl
import com.letsgotoperfection.editor.impl.formatting.TextFormatter
import com.letsgotoperfection.editor.impl.ui.screen.EditorViewModel

/**
 * Factory for creating EditorViewModel without Hilt dependency injection.
 */
class EditorViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            // Create database
            val database = Room.databaseBuilder(
                context.applicationContext,
                EditorDatabase::class.java,
                "editor_database"
            ).fallbackToDestructiveMigration()
                .build()

            // Create repository
            val repository = EditorRepositoryImpl(database.documentDao())

            // Create text formatter
            val textFormatter = TextFormatter()

            // Create ViewModel
            return EditorViewModel(repository, textFormatter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

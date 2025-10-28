package com.letsgotoperfection.systems.editor

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.letsgotoperfection.editor.impl.data.database.EditorDatabase
import com.letsgotoperfection.editor.impl.data.repository.EditorRepositoryImpl
import com.letsgotoperfection.editor.impl.formatting.TextFormatter
import com.letsgotoperfection.editor.impl.ui.screen.EditorScreen
import com.letsgotoperfection.editor.impl.ui.screen.EditorViewModel

/**
 * Wrapper for EditorScreen that provides dependencies without Hilt.
 * This allows the editor to work in apps that don't use Hilt.
 */
@Composable
fun EditorScreenWrapper(
    documentId: String? = null,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: EditorViewModel = viewModel(
        factory = EditorViewModelFactory(context)
    )

    EditorScreen(
        documentId = documentId,
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}

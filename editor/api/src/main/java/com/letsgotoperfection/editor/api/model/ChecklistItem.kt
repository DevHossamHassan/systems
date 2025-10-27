package com.letsgotoperfection.editor.api.model

import androidx.compose.ui.text.AnnotatedString
import java.util.UUID

/**
 * Individual item in a checklist.
 */
data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: AnnotatedString,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val position: Int = 0
)

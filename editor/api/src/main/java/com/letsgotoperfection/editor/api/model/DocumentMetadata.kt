package com.letsgotoperfection.editor.api.model

/**
 * Metadata associated with a document.
 */
data class DocumentMetadata(
    val tags: List<String> = emptyList(),
    val favorite: Boolean = false,
    val archived: Boolean = false,
    val pinned: Boolean = false,
    val color: String? = null,
    val coverImageUri: String? = null,
    val customProperties: Map<String, String> = emptyMap()
)

package com.letsgotoperfection.editor.api.model

/**
 * Metadata for media files.
 */
data class MediaMetadata(
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null, // in milliseconds for video/audio
    val fileSize: Long,
    val mimeType: String,
    val originalFileName: String? = null
)

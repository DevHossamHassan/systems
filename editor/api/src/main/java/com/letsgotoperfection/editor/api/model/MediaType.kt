package com.letsgotoperfection.editor.api.model

/**
 * Supported media types in the editor.
 */
enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    GIF;

    companion object {
        fun fromMimeType(mimeType: String): MediaType? {
            return when {
                mimeType.startsWith("image/gif") -> GIF
                mimeType.startsWith("image/") -> IMAGE
                mimeType.startsWith("video/") -> VIDEO
                mimeType.startsWith("audio/") -> AUDIO
                else -> null
            }
        }
    }
}

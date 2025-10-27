package com.letsgotoperfection.editor.impl.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Type converters for Room database.
 */
class DocumentMetadataConverter {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else json.decodeFromString(value)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun fromStringMap(value: String): Map<String, String> {
        return if (value.isEmpty()) emptyMap() else json.decodeFromString(value)
    }

    @TypeConverter
    fun toStringMap(map: Map<String, String>): String {
        return json.encodeToString(map)
    }
}

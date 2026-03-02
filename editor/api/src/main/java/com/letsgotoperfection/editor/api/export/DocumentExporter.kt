package com.letsgotoperfection.editor.api.export

import com.letsgotoperfection.editor.api.model.EditorDocument

/**
 * Interface for exporting documents to various formats.
 */
interface DocumentExporter {

    /**
     * Export a document to the specified format.
     * @return The exported content as a string, or null if the format is binary (e.g., PDF).
     */
    suspend fun export(document: EditorDocument, format: ExportFormat): Result<String>

    /**
     * Export a document to the specified format and save to a file.
     * @return The file URI where the document was saved.
     */
    suspend fun exportToFile(
        document: EditorDocument,
        format: ExportFormat,
        destinationUri: String
    ): Result<String>
}

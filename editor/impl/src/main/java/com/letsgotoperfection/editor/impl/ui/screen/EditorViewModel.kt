package com.letsgotoperfection.editor.impl.ui.screen

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.editor.api.model.*
import com.letsgotoperfection.editor.api.repository.EditorRepository
import com.letsgotoperfection.editor.impl.formatting.FormattingStyle
import com.letsgotoperfection.editor.impl.formatting.TextFormatter
import com.letsgotoperfection.editor.impl.formatting.UndoRedoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the editor screen.
 */
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: EditorRepository,
    private val textFormatter: TextFormatter = TextFormatter()
) : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val undoRedoManager = UndoRedoManager<EditorDocument>()
    private var autoSaveJob: Job? = null

    init {
        startAutoSave()
    }

    fun loadDocument(documentId: String) {
        viewModelScope.launch {
            repository.getDocument(documentId)?.let { document ->
                _state.update { it.copy(document = document) }
                undoRedoManager.addState(document)
            }
        }
    }

    fun createNewDocument() {
        val newDocument = EditorDocument(
            title = "Untitled",
            blocks = listOf(
                TextBlock(
                    text = AnnotatedString(""),
                    position = 0
                )
            )
        )
        _state.update { it.copy(document = newDocument) }
        undoRedoManager.addState(newDocument)
    }

    fun updateTitle(title: String) {
        _state.update {
            it.copy(document = it.document.copy(title = title))
        }
    }

    fun toggleTitleEdit() {
        _state.update { it.copy(isEditingTitle = !it.isEditingTitle) }
    }

    fun saveDocument() {
        viewModelScope.launch {
            val document = _state.value.document
            val result = if (repository.getDocument(document.id) != null) {
                repository.updateDocument(document)
            } else {
                repository.createDocument(document)
            }

            result.onSuccess {
                _state.update { it.copy(autoSaveStatus = "Saved") }
                delay(2000)
                _state.update { it.copy(autoSaveStatus = null) }
            }.onFailure { error ->
                _state.update { it.copy(autoSaveStatus = "Error: ${error.message}") }
            }
        }
    }

    fun addTextBlock() {
        val newBlock = TextBlock(
            text = AnnotatedString(""),
            position = _state.value.document.blocks.size
        )
        val updatedDocument = _state.value.document.addBlock(newBlock)
        _state.update { it.copy(document = updatedDocument) }
        undoRedoManager.addState(updatedDocument)
    }

    fun addMediaBlock(mediaType: MediaType) {
        // Placeholder - would open media picker
        val newBlock = MediaBlock(
            mediaType = mediaType,
            uri = "",
            metadata = MediaMetadata(
                fileSize = 0,
                mimeType = ""
            ),
            position = _state.value.document.blocks.size
        )
        val updatedDocument = _state.value.document.addBlock(newBlock)
        _state.update { it.copy(document = updatedDocument) }
        undoRedoManager.addState(updatedDocument)
    }

    fun addChecklistBlock() {
        val newBlock = ChecklistBlock(
            items = listOf(
                ChecklistItem(
                    text = AnnotatedString(""),
                    position = 0
                )
            ),
            position = _state.value.document.blocks.size
        )
        val updatedDocument = _state.value.document.addBlock(newBlock)
        _state.update { it.copy(document = updatedDocument) }
        undoRedoManager.addState(updatedDocument)
    }

    fun addCodeBlock() {
        val newBlock = CodeBlock(
            code = "",
            position = _state.value.document.blocks.size
        )
        val updatedDocument = _state.value.document.addBlock(newBlock)
        _state.update { it.copy(document = updatedDocument) }
        undoRedoManager.addState(updatedDocument)
    }

    fun addDividerBlock() {
        val newBlock = DividerBlock(
            position = _state.value.document.blocks.size
        )
        val updatedDocument = _state.value.document.addBlock(newBlock)
        _state.update { it.copy(document = updatedDocument) }
        undoRedoManager.addState(updatedDocument)
    }

    fun updateBlock(block: ContentBlock) {
        val updatedDocument = _state.value.document.updateBlock(block.id) { block }
        _state.update { it.copy(document = updatedDocument) }
    }

    fun deleteBlock(blockId: String) {
        val updatedDocument = _state.value.document.removeBlock(blockId)
        _state.update { it.copy(document = updatedDocument) }
        undoRedoManager.addState(updatedDocument)
    }

    fun setFocusedBlock(blockId: String) {
        _state.update { it.copy(focusedBlockId = blockId) }
    }

    fun toggleBold() {
        applyFormatting(FormattingStyle.BOLD)
    }

    fun toggleItalic() {
        applyFormatting(FormattingStyle.ITALIC)
    }

    fun toggleUnderline() {
        applyFormatting(FormattingStyle.UNDERLINE)
    }

    fun toggleStrikethrough() {
        applyFormatting(FormattingStyle.STRIKETHROUGH)
    }

    fun applyHeading(heading: TextBlockStyle) {
        val focusedBlockId = _state.value.focusedBlockId ?: return
        val block = _state.value.document.blocks.find { it.id == focusedBlockId } as? TextBlock ?: return

        val updatedBlock = block.copy(style = heading)
        updateBlock(updatedBlock)
    }

    fun applyList(listStyle: TextBlockStyle) {
        val focusedBlockId = _state.value.focusedBlockId ?: return
        val block = _state.value.document.blocks.find { it.id == focusedBlockId } as? TextBlock ?: return

        val updatedBlock = block.copy(style = listStyle)
        updateBlock(updatedBlock)
    }

    fun showLinkDialog() {
        // Placeholder - would show link insertion dialog
    }

    fun showInsertMenu() {
        _state.update { it.copy(showInsertMenu = true) }
    }

    fun hideInsertMenu() {
        _state.update { it.copy(showInsertMenu = false) }
    }

    fun undo() {
        val previousDocument = undoRedoManager.undo(_state.value.document)
        if (previousDocument != null) {
            _state.update { it.copy(document = previousDocument) }
        }
    }

    fun redo() {
        val nextDocument = undoRedoManager.redo(_state.value.document)
        if (nextDocument != null) {
            _state.update { it.copy(document = nextDocument) }
        }
    }

    private fun applyFormatting(style: FormattingStyle) {
        val focusedBlockId = _state.value.focusedBlockId ?: return
        val block = _state.value.document.blocks.find { it.id == focusedBlockId } as? TextBlock ?: return
        val selection = _state.value.textSelection ?: return

        val formattedText = textFormatter.toggleStyle(
            text = block.text,
            start = selection.start,
            end = selection.end,
            style = style
        )

        val updatedBlock = block.copy(text = formattedText)
        updateBlock(updatedBlock)
    }

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            state
                .map { it.document }
                .distinctUntilChanged()
                .debounce(3000) // Auto-save after 3 seconds of no changes
                .collect { document ->
                    _state.update { it.copy(autoSaveStatus = "Saving...") }
                    saveDocument()
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
    }
}

/**
 * State for the editor screen.
 */
data class EditorState(
    val document: EditorDocument = EditorDocument(),
    val isEditingTitle: Boolean = false,
    val focusedBlockId: String? = null,
    val textSelection: TextSelection? = null,
    val showFormattingToolbar: Boolean = true,
    val showInsertMenu: Boolean = false,
    val autoSaveStatus: String? = null
)

/**
 * Text selection range.
 */
data class TextSelection(
    val start: Int,
    val end: Int
)

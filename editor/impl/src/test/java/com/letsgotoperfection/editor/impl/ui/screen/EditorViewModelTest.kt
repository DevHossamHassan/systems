package com.letsgotoperfection.editor.impl.ui.screen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.letsgotoperfection.editor.api.model.*
import com.letsgotoperfection.editor.impl.fake.FakeEditorRepository
import com.letsgotoperfection.editor.impl.formatting.TextFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for EditorViewModel following Google's best practices.
 * Uses FakeEditorRepository instead of mocks for more realistic testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditorViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeEditorRepository
    private lateinit var textFormatter: TextFormatter
    private lateinit var viewModel: EditorViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeEditorRepository()
        textFormatter = TextFormatter()
        viewModel = EditorViewModel(repository, textFormatter)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        repository.clearAllDocuments()
    }

    @Test
    fun `createNewDocument creates document with default title and one text block`() = runTest {
        // When
        viewModel.createNewDocument()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("Untitled", state.document.title)
        assertEquals(1, state.document.blocks.size)
        assertTrue(state.document.blocks.first() is TextBlock)
    }

    @Test
    fun `addTextBlock adds new text block to document state`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()
        val initialBlockCount = viewModel.state.value.document.blocks.size

        // When
        viewModel.addTextBlock()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(initialBlockCount + 1, state.document.blocks.size)
        assertTrue(state.document.blocks.last() is TextBlock)
    }

    @Test
    fun `addTextBlock updates UI state immediately`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        viewModel.state.test {
            val initial = awaitItem()
            val initialCount = initial.document.blocks.size

            // When
            viewModel.addTextBlock()
            advanceUntilIdle()

            // Then
            val updated = expectMostRecentItem()
            assertEquals(initialCount + 1, updated.document.blocks.size)
        }
    }

    @Test
    fun `addMediaBlock adds media block with correct type`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        viewModel.addMediaBlock(MediaType.IMAGE)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val mediaBlock = state.document.blocks.last() as MediaBlock
        assertEquals(MediaType.IMAGE, mediaBlock.mediaType)
    }

    @Test
    fun `addMediaBlock VIDEO adds video media block`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        viewModel.addMediaBlock(MediaType.VIDEO)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val mediaBlock = state.document.blocks.last() as MediaBlock
        assertEquals(MediaType.VIDEO, mediaBlock.mediaType)
    }

    @Test
    fun `addChecklistBlock adds checklist with one empty item`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        viewModel.addChecklistBlock()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val checklistBlock = state.document.blocks.last() as ChecklistBlock
        assertEquals(1, checklistBlock.items.size)
        assertEquals("", checklistBlock.items.first().text.text)
        assertFalse(checklistBlock.items.first().isChecked)
    }

    @Test
    fun `addCodeBlock adds code block to document`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        viewModel.addCodeBlock()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.document.blocks.last() is CodeBlock)
        assertEquals("", (state.document.blocks.last() as CodeBlock).code)
    }

    @Test
    fun `addDividerBlock adds divider to document`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        viewModel.addDividerBlock()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.document.blocks.last() is DividerBlock)
    }

    @Test
    fun `updateBlock updates existing block in document`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        val originalBlock = viewModel.state.value.document.blocks.first() as TextBlock
        val updatedText = "Updated text content"

        // When
        val updatedBlock = originalBlock.copy(
            text = androidx.compose.ui.text.AnnotatedString(updatedText)
        )
        viewModel.updateBlock(updatedBlock)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        val resultBlock = state.document.blocks.first() as TextBlock
        assertEquals(updatedText, resultBlock.text.text)
    }

    @Test
    fun `deleteBlock removes block from document`() = runTest {
        // Given
        viewModel.createNewDocument()
        viewModel.addTextBlock()
        advanceUntilIdle()

        val blockToDelete = viewModel.state.value.document.blocks.last()
        val initialCount = viewModel.state.value.document.blocks.size

        // When
        viewModel.deleteBlock(blockToDelete.id)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(initialCount - 1, state.document.blocks.size)
        assertTrue(state.document.blocks.none { it.id == blockToDelete.id })
    }

    @Test
    fun `updateTitle updates document title`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()
        val newTitle = "My Amazing Document"

        // When
        viewModel.updateTitle(newTitle)
        advanceUntilIdle()

        // Then
        assertEquals(newTitle, viewModel.state.value.document.title)
    }

    @Test
    fun `toggleTitleEdit toggles editing state`() = runTest {
        // Given
        val initialState = viewModel.state.value.isEditingTitle

        // When
        viewModel.toggleTitleEdit()
        advanceUntilIdle()

        // Then
        assertEquals(!initialState, viewModel.state.value.isEditingTitle)

        // And toggle again
        viewModel.toggleTitleEdit()
        advanceUntilIdle()
        assertEquals(initialState, viewModel.state.value.isEditingTitle)
    }

    @Test
    fun `saveDocument persists new document to repository`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()
        val documentId = viewModel.state.value.document.id

        // When
        viewModel.saveDocument()
        testScheduler.advanceTimeBy(100) // Advance a bit to process the save
        testScheduler.runCurrent()

        // Then
        assertTrue(repository.hasDocument(documentId))
        // Note: autoSaveStatus may be "Saved" or null depending on timing
        // The important thing is the document is persisted
    }

    @Test
    fun `saveDocument updates existing document in repository`() = runTest {
        // Given
        val existingDocument = EditorDocument(id = "test-id", title = "Existing")
        repository.createDocument(existingDocument)
        viewModel.loadDocument("test-id")
        advanceUntilIdle()

        // When
        viewModel.updateTitle("Updated Title")
        viewModel.saveDocument()
        advanceUntilIdle()

        // Then
        val savedDoc = repository.getDocument("test-id")
        assertEquals("Updated Title", savedDoc?.title)
    }

    @Test
    fun `saveDocument shows error status on repository failure`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()
        repository.shouldReturnError = true
        repository.errorMessage = "Save failed"

        // When
        viewModel.saveDocument()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.autoSaveStatus?.contains("Error") == true)
    }

    @Test
    fun `loadDocument loads document from repository`() = runTest {
        // Given
        val documentId = "test-doc-id"
        val document = EditorDocument(
            id = documentId,
            title = "Test Document",
            blocks = listOf(
                TextBlock(
                    text = androidx.compose.ui.text.AnnotatedString("Content"),
                    position = 0
                )
            )
        )
        repository.createDocument(document)

        // When
        viewModel.loadDocument(documentId)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(documentId, state.document.id)
        assertEquals("Test Document", state.document.title)
        assertEquals(1, state.document.blocks.size)
    }

    @Test
    fun `loadDocument handles non-existent document gracefully`() = runTest {
        // When
        viewModel.loadDocument("non-existent-id")
        advanceUntilIdle()

        // Then - should not crash, state should remain unchanged or default
        val state = viewModel.state.value
        assertTrue(state.document.id.isNotEmpty())
    }

    @Test
    fun `showInsertMenu sets showInsertMenu to true`() = runTest {
        // When
        viewModel.showInsertMenu()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.showInsertMenu)
    }

    @Test
    fun `hideInsertMenu sets showInsertMenu to false`() = runTest {
        // Given
        viewModel.showInsertMenu()
        advanceUntilIdle()

        // When
        viewModel.hideInsertMenu()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.showInsertMenu)
    }

    @Test
    fun `setFocusedBlock updates focused block id`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()
        val blockId = viewModel.state.value.document.blocks.first().id

        // When
        viewModel.setFocusedBlock(blockId)
        advanceUntilIdle()

        // Then
        assertEquals(blockId, viewModel.state.value.focusedBlockId)
    }

    @Test
    fun `multiple addTextBlock calls add multiple blocks`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()
        val initialCount = viewModel.state.value.document.blocks.size

        // When
        viewModel.addTextBlock()
        viewModel.addTextBlock()
        viewModel.addTextBlock()
        advanceUntilIdle()

        // Then
        assertEquals(initialCount + 3, viewModel.state.value.document.blocks.size)
    }

    @Test
    fun `createNewDocument resets document state`() = runTest {
        // Given
        viewModel.createNewDocument()
        viewModel.addTextBlock()
        viewModel.addTextBlock()
        viewModel.updateTitle("Modified")
        advanceUntilIdle()

        // When
        viewModel.createNewDocument()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("Untitled", state.document.title)
        assertEquals(1, state.document.blocks.size)
    }

    @Test
    fun `adding mixed block types creates correct block instances`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        viewModel.addTextBlock()
        viewModel.addMediaBlock(MediaType.IMAGE)
        viewModel.addChecklistBlock()
        viewModel.addCodeBlock()
        viewModel.addDividerBlock()
        advanceUntilIdle()

        // Then
        val blocks = viewModel.state.value.document.blocks
        assertEquals(6, blocks.size) // 1 initial + 5 added
        assertTrue(blocks[0] is TextBlock)
        assertTrue(blocks[1] is TextBlock)
        assertTrue(blocks[2] is MediaBlock)
        assertTrue(blocks[3] is ChecklistBlock)
        assertTrue(blocks[4] is CodeBlock)
        assertTrue(blocks[5] is DividerBlock)
    }

    @Test
    fun `document word count updates when blocks change`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        val firstBlock = viewModel.state.value.document.blocks.first() as TextBlock
        val updatedBlock = firstBlock.copy(
            text = androidx.compose.ui.text.AnnotatedString("Hello world test")
        )
        viewModel.updateBlock(updatedBlock)
        advanceUntilIdle()

        // Then
        val wordCount = viewModel.state.value.document.wordCount()
        assertEquals(3, wordCount)
    }

    @Test
    fun `document character count updates when blocks change`() = runTest {
        // Given
        viewModel.createNewDocument()
        advanceUntilIdle()

        // When
        val firstBlock = viewModel.state.value.document.blocks.first() as TextBlock
        val testText = "Test"
        val updatedBlock = firstBlock.copy(
            text = androidx.compose.ui.text.AnnotatedString(testText)
        )
        viewModel.updateBlock(updatedBlock)
        advanceUntilIdle()

        // Then
        val charCount = viewModel.state.value.document.characterCount()
        assertEquals(testText.length, charCount)
    }
}

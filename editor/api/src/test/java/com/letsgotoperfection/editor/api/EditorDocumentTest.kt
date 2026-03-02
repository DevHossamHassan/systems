package com.letsgotoperfection.editor.api

import androidx.compose.ui.text.AnnotatedString
import com.letsgotoperfection.editor.api.model.EditorDocument
import com.letsgotoperfection.editor.api.model.TextBlock
import com.letsgotoperfection.editor.api.model.TextBlockStyle
import org.junit.Assert.*
import org.junit.Test

class EditorDocumentTest {

    @Test
    fun `addBlock adds block to document`() {
        val document = EditorDocument(title = "Test")
        val block = TextBlock(text = AnnotatedString("Hello"))

        val updated = document.addBlock(block)

        assertEquals(1, updated.blocks.size)
        assertEquals(block, updated.blocks.first())
    }

    @Test
    fun `removeBlock removes block from document`() {
        val block = TextBlock(text = AnnotatedString("Hello"))
        val document = EditorDocument(title = "Test", blocks = listOf(block))

        val updated = document.removeBlock(block.id)

        assertEquals(0, updated.blocks.size)
    }

    @Test
    fun `wordCount returns correct count`() {
        val block1 = TextBlock(text = AnnotatedString("Hello world"))
        val block2 = TextBlock(text = AnnotatedString("Test document"))
        val document = EditorDocument(blocks = listOf(block1, block2))

        assertEquals(4, document.wordCount())
    }

    @Test
    fun `characterCount returns correct count`() {
        val block = TextBlock(text = AnnotatedString("Hello"))
        val document = EditorDocument(blocks = listOf(block))

        assertEquals(5, document.characterCount())
    }

    @Test
    fun `updateBlock modifies existing block`() {
        val block = TextBlock(text = AnnotatedString("Original"))
        val document = EditorDocument(blocks = listOf(block))

        val updated = document.updateBlock(block.id) { b ->
            (b as TextBlock).copy(text = AnnotatedString("Updated"))
        }

        val updatedBlock = updated.blocks.first() as TextBlock
        assertEquals("Updated", updatedBlock.text.text)
    }
}

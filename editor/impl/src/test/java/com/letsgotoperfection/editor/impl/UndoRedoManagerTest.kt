package com.letsgotoperfection.editor.impl

import com.letsgotoperfection.editor.impl.formatting.UndoRedoManager
import org.junit.Assert.*
import org.junit.Test

class UndoRedoManagerTest {

    @Test
    fun `undo returns previous state`() {
        val manager = UndoRedoManager<String>()
        manager.addState("state1")
        manager.addState("state2")

        val result = manager.undo("state3")

        assertEquals("state2", result)
    }

    @Test
    fun `redo returns next state after undo`() {
        val manager = UndoRedoManager<String>()
        manager.addState("state1")
        manager.addState("state2")
        manager.undo("state3")

        val result = manager.redo("state2")

        assertEquals("state3", result)
    }

    @Test
    fun `canUndo returns true when history exists`() {
        val manager = UndoRedoManager<String>()
        manager.addState("state1")

        assertTrue(manager.canUndo())
    }

    @Test
    fun `canUndo returns false when history is empty`() {
        val manager = UndoRedoManager<String>()

        assertFalse(manager.canUndo())
    }

    @Test
    fun `addState clears redo stack`() {
        val manager = UndoRedoManager<String>()
        manager.addState("state1")
        manager.addState("state2")
        manager.undo("state3")
        manager.addState("state4")

        assertFalse(manager.canRedo())
    }
}

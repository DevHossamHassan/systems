package com.letsgotoperfection.editor.impl.formatting

import java.util.Stack

/**
 * Manages undo/redo history for the editor.
 */
class UndoRedoManager<T>(
    private val maxHistorySize: Int = 100
) {
    private val undoStack = Stack<T>()
    private val redoStack = Stack<T>()

    /**
     * Add a new state to the history.
     */
    fun addState(state: T) {
        undoStack.push(state)
        redoStack.clear()

        // Limit stack size
        while (undoStack.size > maxHistorySize) {
            undoStack.removeAt(0)
        }
    }

    /**
     * Undo the last action and return the previous state.
     */
    fun undo(currentState: T): T? {
        if (undoStack.isEmpty()) return null

        redoStack.push(currentState)
        return undoStack.pop()
    }

    /**
     * Redo the last undone action.
     */
    fun redo(currentState: T): T? {
        if (redoStack.isEmpty()) return null

        undoStack.push(currentState)
        return redoStack.pop()
    }

    /**
     * Check if undo is available.
     */
    fun canUndo(): Boolean = undoStack.isNotEmpty()

    /**
     * Check if redo is available.
     */
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    /**
     * Clear all history.
     */
    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}

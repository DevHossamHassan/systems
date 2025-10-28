package com.letsgotoperfection.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.letsgotoperfection.editor.api.model.*
import java.time.Instant

/**
 * Journal screen that demonstrates integration with the editor module.
 * This screen shows a list of journal entries and allows creating new ones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onCreateEntry: () -> Unit,
    onEntryClick: (String) -> Unit
) {
    // Demo data showing editor API usage
    val sampleEntries = remember {
        listOf(
            createSampleJournalEntry("Morning Thoughts", "Feeling grateful today..."),
            createSampleJournalEntry("Project Ideas", "Working on a new Android app..."),
            createSampleJournalEntry("Daily Reflection", "What did I learn today?")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEntry) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleEntries) { entry ->
                JournalEntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryCard(
    entry: EditorDocument,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Show first text block as preview
            entry.blocks.firstOrNull()?.let { block ->
                when (block) {
                    is TextBlock -> {
                        Text(
                            text = block.text.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                    else -> {
                        Text(
                            text = "Contains ${block::class.simpleName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Creates a sample journal entry using the editor API models.
 * This demonstrates how feature modules can use the editor API.
 */
private fun createSampleJournalEntry(title: String, content: String): EditorDocument {
    return EditorDocument(
        id = java.util.UUID.randomUUID().toString(),
        title = title,
        blocks = listOf(
            TextBlock(
                id = java.util.UUID.randomUUID().toString(),
                text = androidx.compose.ui.text.AnnotatedString(content),
                style = TextBlockStyle.Body
            )
        ),
        metadata = DocumentMetadata(
            tags = listOf("journal"),
            favorite = false
        ),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}

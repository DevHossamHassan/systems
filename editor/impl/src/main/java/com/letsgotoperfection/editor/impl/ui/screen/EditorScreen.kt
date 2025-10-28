package com.letsgotoperfection.editor.impl.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.editor.api.model.*

/**
 * Main editor screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    documentId: String? = null,
    viewModel: EditorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(documentId) {
        if (documentId != null) {
            viewModel.loadDocument(documentId)
        } else {
            viewModel.createNewDocument()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (state.isEditingTitle) {
                        TextField(
                            value = state.document.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            placeholder = { Text("Untitled") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    } else {
                        Text(
                            text = state.document.title.ifEmpty { "Untitled" },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleTitleEdit() }) {
                        Icon(Icons.Default.Edit, "Edit title")
                    }
                    IconButton(onClick = { viewModel.saveDocument() }) {
                        Icon(Icons.Default.Save, "Save")
                    }
                    IconButton(onClick = { /* Show more menu */ }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${state.document.wordCount()} words, ${state.document.characterCount()} characters"
                    )
                    if (state.autoSaveStatus != null) {
                        Text(
                            text = state.autoSaveStatus ?: "",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showInsertMenu() }
            ) {
                Icon(Icons.Default.Add, "Add block")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Formatting toolbar
            if (state.showFormattingToolbar) {
                FormattingToolbar(
                    onBoldClick = { viewModel.toggleBold() },
                    onItalicClick = { viewModel.toggleItalic() },
                    onUnderlineClick = { viewModel.toggleUnderline() },
                    onStrikethroughClick = { viewModel.toggleStrikethrough() },
                    onHeadingClick = { heading -> viewModel.applyHeading(heading) },
                    onListClick = { listType -> viewModel.applyList(listType) },
                    onInsertLink = { viewModel.showLinkDialog() },
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider()
            }

            // Content blocks
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.document.blocks,
                    key = { it.id }
                ) { block ->
                    BlockRenderer(
                        block = block,
                        onBlockUpdate = { updatedBlock ->
                            viewModel.updateBlock(updatedBlock)
                        },
                        onBlockDelete = {
                            viewModel.deleteBlock(block.id)
                        },
                        onBlockFocus = {
                            viewModel.setFocusedBlock(block.id)
                        }
                    )
                }

                // Add new block button
                item {
                    TextButton(
                        onClick = { viewModel.addTextBlock() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, "Add block")
                        Spacer(Modifier.width(8.dp))
                        Text("Add block")
                    }
                }
            }
        }
    }

    // Insert menu dialog
    if (state.showInsertMenu) {
        InsertMenuDialog(
            onDismiss = { viewModel.hideInsertMenu() },
            onInsertText = { viewModel.addTextBlock() },
            onInsertImage = { viewModel.addMediaBlock(MediaType.IMAGE) },
            onInsertVideo = { viewModel.addMediaBlock(MediaType.VIDEO) },
            onInsertChecklist = { viewModel.addChecklistBlock() },
            onInsertDivider = { viewModel.addDividerBlock() },
            onInsertCode = { viewModel.addCodeBlock() }
        )
    }
}

@Composable
private fun BlockRenderer(
    block: ContentBlock,
    onBlockUpdate: (ContentBlock) -> Unit,
    onBlockDelete: () -> Unit,
    onBlockFocus: () -> Unit
) {
    when (block) {
        is TextBlock -> TextBlockView(
            block = block,
            onUpdate = onBlockUpdate,
            onDelete = onBlockDelete,
            onFocus = onBlockFocus
        )
        is MediaBlock -> MediaBlockView(
            block = block,
            onUpdate = onBlockUpdate,
            onDelete = onBlockDelete
        )
        is ChecklistBlock -> ChecklistBlockView(
            block = block,
            onUpdate = onBlockUpdate,
            onDelete = onBlockDelete
        )
        is FileAttachmentBlock -> FileAttachmentBlockView(
            block = block,
            onDelete = onBlockDelete
        )
        is DividerBlock -> DividerBlockView(block = block)
        is CodeBlock -> CodeBlockView(
            block = block,
            onUpdate = onBlockUpdate,
            onDelete = onBlockDelete
        )
    }
}

@Composable
private fun FormattingToolbar(
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onStrikethroughClick: () -> Unit,
    onHeadingClick: (TextBlockStyle) -> Unit,
    onListClick: (TextBlockStyle) -> Unit,
    onInsertLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onBoldClick) {
            Icon(Icons.Default.FormatBold, "Bold")
        }
        IconButton(onClick = onItalicClick) {
            Icon(Icons.Default.FormatItalic, "Italic")
        }
        IconButton(onClick = onUnderlineClick) {
            Icon(Icons.Default.FormatUnderlined, "Underline")
        }
        IconButton(onClick = onStrikethroughClick) {
            Icon(Icons.Default.FormatStrikethrough, "Strikethrough")
        }
        VerticalDivider()
        IconButton(onClick = { onHeadingClick(TextBlockStyle.Heading1) }) {
            Text("H1", style = MaterialTheme.typography.labelSmall)
        }
        IconButton(onClick = { onHeadingClick(TextBlockStyle.Heading2) }) {
            Text("H2", style = MaterialTheme.typography.labelSmall)
        }
        VerticalDivider()
        IconButton(onClick = { onListClick(TextBlockStyle.UnorderedList) }) {
            Icon(Icons.Default.FormatListBulleted, "Bullet list")
        }
        IconButton(onClick = { onListClick(TextBlockStyle.OrderedList) }) {
            Icon(Icons.Default.FormatListNumbered, "Numbered list")
        }
        VerticalDivider()
        IconButton(onClick = onInsertLink) {
            Icon(Icons.Default.Link, "Insert link")
        }
    }
}

@Composable
private fun InsertMenuDialog(
    onDismiss: () -> Unit,
    onInsertText: () -> Unit,
    onInsertImage: () -> Unit,
    onInsertVideo: () -> Unit,
    onInsertChecklist: () -> Unit,
    onInsertDivider: () -> Unit,
    onInsertCode: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Insert block") },
        text = {
            Column {
                TextButton(
                    onClick = {
                        onInsertText()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.TextFields, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Text")
                }
                TextButton(
                    onClick = {
                        onInsertImage()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Image")
                }
                TextButton(
                    onClick = {
                        onInsertVideo()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VideoLibrary, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Video")
                }
                TextButton(
                    onClick = {
                        onInsertChecklist()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Checklist, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Checklist")
                }
                TextButton(
                    onClick = {
                        onInsertCode()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Code, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Code block")
                }
                TextButton(
                    onClick = {
                        onInsertDivider()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.HorizontalRule, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Divider")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Placeholder composables for block views
@Composable
private fun TextBlockView(
    block: TextBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit,
    onFocus: () -> Unit
) {
    var text by remember(block.id) { mutableStateOf(block.text.text) }

    TextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            val updatedBlock = block.copy(
                text = androidx.compose.ui.text.AnnotatedString(newText)
            )
            onUpdate(updatedBlock)
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        placeholder = { Text("Type something...") },
        minLines = 6,
        maxLines = Int.MAX_VALUE,
        textStyle = when (block.style) {
            is TextBlockStyle.Heading1 -> MaterialTheme.typography.headlineLarge
            is TextBlockStyle.Heading2 -> MaterialTheme.typography.headlineMedium
            is TextBlockStyle.Heading3 -> MaterialTheme.typography.headlineSmall
            is TextBlockStyle.Heading4 -> MaterialTheme.typography.titleLarge
            is TextBlockStyle.Heading5 -> MaterialTheme.typography.titleMedium
            is TextBlockStyle.Heading6 -> MaterialTheme.typography.titleSmall
            else -> MaterialTheme.typography.bodyLarge
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun MediaBlockView(
    block: MediaBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text("Media: ${block.mediaType}", modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun ChecklistBlockView(
    block: ChecklistBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        block.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { checked ->
                        val updatedItems = block.items.toMutableList()
                        updatedItems[index] = item.copy(
                            isChecked = checked,
                            completedAt = if (checked) System.currentTimeMillis() else null
                        )
                        val updatedBlock = block.copy(items = updatedItems)
                        onUpdate(updatedBlock)
                    }
                )
                Spacer(Modifier.width(8.dp))
                var itemText by remember(item.id) { mutableStateOf(item.text.text) }
                TextField(
                    value = itemText,
                    onValueChange = { newText ->
                        itemText = newText
                        val updatedItems = block.items.toMutableList()
                        updatedItems[index] = item.copy(
                            text = androidx.compose.ui.text.AnnotatedString(newText)
                        )
                        val updatedBlock = block.copy(items = updatedItems)
                        onUpdate(updatedBlock)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("List item") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        // Add new item button
        TextButton(
            onClick = {
                val newItem = ChecklistItem(
                    text = androidx.compose.ui.text.AnnotatedString(""),
                    position = block.items.size
                )
                val updatedBlock = block.copy(items = block.items + newItem)
                onUpdate(updatedBlock)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Add item")
        }
    }
}

@Composable
private fun FileAttachmentBlockView(
    block: FileAttachmentBlock,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text("File: ${block.fileName}", modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun DividerBlockView(block: DividerBlock) {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun CodeBlockView(
    block: CodeBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit
) {
    var code by remember(block.id) { mutableStateOf(block.code) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        TextField(
            value = code,
            onValueChange = { newCode ->
                code = newCode
                val updatedBlock = block.copy(code = newCode)
                onUpdate(updatedBlock)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Enter code...") },
            minLines = 5,
            maxLines = Int.MAX_VALUE,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

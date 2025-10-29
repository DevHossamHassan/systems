package com.letsgotoperfection.editor.impl.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.editor.api.model.*

/**
 * Main editor screen composable - Text-first editor with block insertion.
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

            // Content blocks - Text-first approach
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // If no blocks exist, show a default text block
                if (state.document.blocks.isEmpty()) {
                    item {
                        Text(
                            text = "Start typing...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
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
                }

                // Add new block button
                item {
                    OutlinedButton(
                        onClick = { viewModel.addTextBlock() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add text block")
                        Spacer(Modifier.width(8.dp))
                        Text("Add text block")
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
internal fun BlockRenderer(
    block: ContentBlock,
    onBlockUpdate: (ContentBlock) -> Unit,
    onBlockDelete: () -> Unit,
    onBlockFocus: () -> Unit
) {
    when (block) {
        is TextBlock -> TextBlockView(
            block = block,
            onUpdate = { onBlockUpdate(it) },
            onDelete = onBlockDelete,
            onFocus = onBlockFocus
        )
        is MediaBlock -> MediaBlockView(
            block = block,
            onUpdate = { onBlockUpdate(it) },
            onDelete = onBlockDelete
        )
        is ChecklistBlock -> ChecklistBlockView(
            block = block,
            onUpdate = { onBlockUpdate(it) },
            onDelete = onBlockDelete
        )
        is FileAttachmentBlock -> FileAttachmentBlockView(
            block = block,
            onDelete = onBlockDelete
        )
        is DividerBlock -> DividerBlockView(block = block)
        is CodeBlock -> CodeBlockView(
            block = block,
            onUpdate = { onBlockUpdate(it) },
            onDelete = onBlockDelete
        )
    }
}

@Composable
internal fun FormattingToolbar(
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit,
    onStrikethroughClick: () -> Unit,
    onHeadingClick: (TextBlockStyle) -> Unit,
    onListClick: (TextBlockStyle) -> Unit,
    onInsertLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Text formatting
            IconButton(onClick = onBoldClick) {
                Text("B", style = MaterialTheme.typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
            }
            IconButton(onClick = onItalicClick) {
                Text("I", style = MaterialTheme.typography.labelLarge.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
            }
            IconButton(onClick = onUnderlineClick) {
                Text("U", style = MaterialTheme.typography.labelLarge.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline))
            }
            IconButton(onClick = onStrikethroughClick) {
                Text("S", style = MaterialTheme.typography.labelLarge.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough))
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .padding(horizontal = 4.dp)
            )

            // Headings
            IconButton(onClick = { onHeadingClick(TextBlockStyle.Heading1) }) {
                Text("H1", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { onHeadingClick(TextBlockStyle.Heading2) }) {
                Text("H2", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = { onHeadingClick(TextBlockStyle.Heading3) }) {
                Text("H3", style = MaterialTheme.typography.labelSmall)
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .padding(horizontal = 4.dp)
            )

            // Lists
            IconButton(onClick = { onListClick(TextBlockStyle.UnorderedList) }) {
                Icon(Icons.Default.List, "Bullet list")
            }
            IconButton(onClick = { onListClick(TextBlockStyle.OrderedList) }) {
                Icon(Icons.Default.FilterList, "Numbered list")
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .padding(horizontal = 4.dp)
            )

            // Link
            IconButton(onClick = onInsertLink) {
                Icon(Icons.Default.Link, "Insert link")
            }
        }
    }
}

@Composable
internal fun InsertMenuDialog(
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
        title = { Text("Insert Block") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onInsertText()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Create, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Text", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                }
                OutlinedButton(
                    onClick = {
                        onInsertImage()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Image", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                }
                OutlinedButton(
                    onClick = {
                        onInsertVideo()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Video", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                }
                OutlinedButton(
                    onClick = {
                        onInsertChecklist()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Checklist", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                }
                OutlinedButton(
                    onClick = {
                        onInsertCode()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Code, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Code Block", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                }
                OutlinedButton(
                    onClick = {
                        onInsertDivider()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Remove, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Divider", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
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

// Block view composables
@Composable
internal fun TextBlockView(
    block: TextBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit,
    onFocus: () -> Unit
) {
    var text by remember(block.id) { mutableStateOf(block.text.text) }

    OutlinedTextField(
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
            .heightIn(min = 80.dp),
        placeholder = { Text("Start typing...") },
        minLines = 3,
        textStyle = when (block.style) {
            is TextBlockStyle.Heading1 -> MaterialTheme.typography.headlineLarge
            is TextBlockStyle.Heading2 -> MaterialTheme.typography.headlineMedium
            is TextBlockStyle.Heading3 -> MaterialTheme.typography.headlineSmall
            is TextBlockStyle.Heading4 -> MaterialTheme.typography.titleLarge
            is TextBlockStyle.Heading5 -> MaterialTheme.typography.titleMedium
            is TextBlockStyle.Heading6 -> MaterialTheme.typography.titleSmall
            else -> MaterialTheme.typography.bodyLarge
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        trailingIcon = {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete block",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
internal fun MediaBlockView(
    block: MediaBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (block.mediaType == MediaType.IMAGE) Icons.Default.Image else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (block.mediaType == MediaType.IMAGE) "Image" else "Video",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = block.uri,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
internal fun ChecklistBlockView(
    block: ChecklistBlock,
    onUpdate: (ContentBlock) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Header with delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Checklist",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete checklist",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Checklist items
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
                    OutlinedTextField(
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
                        textStyle = MaterialTheme.typography.bodyMedium
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
}

@Composable
internal fun FileAttachmentBlockView(
    block: FileAttachmentBlock,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = block.fileName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "File attachment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
internal fun DividerBlockView(block: DividerBlock) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 16.dp),
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
internal fun CodeBlockView(
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
        Column(modifier = Modifier.padding(8.dp)) {
            // Header with language and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = block.language ?: "Code",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete code block",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Code editor
            OutlinedTextField(
                value = code,
                onValueChange = { newCode ->
                    code = newCode
                    val updatedBlock = block.copy(code = newCode)
                    onUpdate(updatedBlock)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter code...") },
                minLines = 5,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
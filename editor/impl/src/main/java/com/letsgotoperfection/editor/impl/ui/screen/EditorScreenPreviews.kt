package com.letsgotoperfection.editor.impl.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.letsgotoperfection.editor.api.model.*

/**
 * Preview theme wrapper for consistent theming
 */
@Composable
private fun EditorPreviewTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme {
        Surface {
            content()
        }
    }
}

// ============================================
// Component-Level Previews
// ============================================

/**
 * Preview: Formatting Toolbar
 */
@PreviewLightDark
@Composable
private fun PreviewFormattingToolbar() {
    EditorPreviewTheme {
        FormattingToolbar(
            onBoldClick = {},
            onItalicClick = {},
            onUnderlineClick = {},
            onStrikethroughClick = {},
            onHeadingClick = {},
            onListClick = {},
            onInsertLink = {}
        )
    }
}

/**
 * Preview: Text Block - Body
 */
@Preview(
    name = "Text Block - Body",
    showBackground = true
)
@Composable
private fun PreviewTextBlockBody() {
    EditorPreviewTheme {
        TextBlockView(
            block = TextBlock(
                id = "1",
                text = AnnotatedString("This is a regular paragraph with some sample text content that demonstrates the text block view."),
                style = TextBlockStyle.Body,
                position = 0
            ),
            onUpdate = {},
            onDelete = {},
            onFocus = {}
        )
    }
}

/**
 * Preview: Text Block - Heading 1
 */
@Preview(
    name = "Text Block - Heading 1",
    showBackground = true
)
@Composable
private fun PreviewTextBlockHeading1() {
    EditorPreviewTheme {
        TextBlockView(
            block = TextBlock(
                id = "2",
                text = AnnotatedString("Main Heading"),
                style = TextBlockStyle.Heading1,
                position = 0
            ),
            onUpdate = {},
            onDelete = {},
            onFocus = {}
        )
    }
}

/**
 * Preview: Text Block - Heading 2
 */
@Preview(
    name = "Text Block - Heading 2",
    showBackground = true
)
@Composable
private fun PreviewTextBlockHeading2() {
    EditorPreviewTheme {
        TextBlockView(
            block = TextBlock(
                id = "3",
                text = AnnotatedString("Section Heading"),
                style = TextBlockStyle.Heading2,
                position = 0
            ),
            onUpdate = {},
            onDelete = {},
            onFocus = {}
        )
    }
}

/**
 * Preview: Media Block - Image
 */
@Preview(
    name = "Media Block - Image",
    showBackground = true
)
@Composable
private fun PreviewMediaBlockImage() {
    EditorPreviewTheme {
        MediaBlockView(
            block = MediaBlock(
                id = "4",
                mediaType = MediaType.IMAGE,
                uri = "https://example.com/beautiful-sunset.jpg",
                metadata = MediaMetadata(
                    width = 1920,
                    height = 1080,
                    fileSize = 1024000,
                    mimeType = "image/jpeg"
                ),
                position = 0
            ),
            onUpdate = {},
            onDelete = {}
        )
    }
}

/**
 * Preview: Media Block - Video
 */
@Preview(
    name = "Media Block - Video",
    showBackground = true
)
@Composable
private fun PreviewMediaBlockVideo() {
    EditorPreviewTheme {
        MediaBlockView(
            block = MediaBlock(
                id = "5",
                mediaType = MediaType.VIDEO,
                uri = "https://example.com/tutorial-video.mp4",
                metadata = MediaMetadata(
                    width = 1920,
                    height = 1080,
                    duration = 120000,
                    fileSize = 5024000,
                    mimeType = "video/mp4"
                ),
                position = 0
            ),
            onUpdate = {},
            onDelete = {}
        )
    }
}

/**
 * Preview: Checklist Block
 */
@Preview(
    name = "Checklist Block",
    showBackground = true
)
@Composable
private fun PreviewChecklistBlock() {
    EditorPreviewTheme {
        ChecklistBlockView(
            block = ChecklistBlock(
                id = "6",
                items = listOf(
                    ChecklistItem(
                        id = "c1",
                        text = AnnotatedString("Completed task"),
                        isChecked = true,
                        position = 0,
                        completedAt = System.currentTimeMillis()
                    ),
                    ChecklistItem(
                        id = "c2",
                        text = AnnotatedString("Pending task"),
                        isChecked = false,
                        position = 1
                    ),
                    ChecklistItem(
                        id = "c3",
                        text = AnnotatedString("Another task to do"),
                        isChecked = false,
                        position = 2
                    )
                ),
                position = 0
            ),
            onUpdate = {},
            onDelete = {}
        )
    }
}

/**
 * Preview: Code Block
 */
@Preview(
    name = "Code Block",
    showBackground = true
)
@Composable
private fun PreviewCodeBlock() {
    EditorPreviewTheme {
        CodeBlockView(
            block = CodeBlock(
                id = "7",
                code = "fun calculateSum(a: Int, b: Int): Int {\n    return a + b\n}\n\nfun main() {\n    val result = calculateSum(5, 3)\n    println(\"Sum: \$result\")\n}",
                language = "Kotlin",
                position = 0
            ),
            onUpdate = {},
            onDelete = {}
        )
    }
}

/**
 * Preview: File Attachment Block
 */
@Preview(
    name = "File Attachment Block",
    showBackground = true
)
@Composable
private fun PreviewFileAttachmentBlock() {
    EditorPreviewTheme {
        FileAttachmentBlockView(
            block = FileAttachmentBlock(
                id = "8",
                fileName = "project-specification.pdf",
                fileUri = "https://example.com/files/spec.pdf",
                fileSize = 2048576L, // 2 MB
                mimeType = "application/pdf",
                position = 0
            ),
            onDelete = {}
        )
    }
}

/**
 * Preview: Divider Block
 */
@Preview(
    name = "Divider Block",
    showBackground = true
)
@Composable
private fun PreviewDividerBlock() {
    EditorPreviewTheme {
        DividerBlockView(
            block = DividerBlock(
                id = "9",
                position = 0
            )
        )
    }
}

/**
 * Preview: Insert Menu Dialog
 */
@Preview(
    name = "Insert Menu Dialog",
    showBackground = true
)
@Composable
private fun PreviewInsertMenuDialog() {
    EditorPreviewTheme {
        InsertMenuDialog(
            onDismiss = {},
            onInsertText = {},
            onInsertImage = {},
            onInsertVideo = {},
            onInsertChecklist = {},
            onInsertDivider = {},
            onInsertCode = {}
        )
    }
}

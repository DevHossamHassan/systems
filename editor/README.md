# World-Class Text Editor Module

A modern, modular, enterprise-grade rich text editor for Android built with Jetpack Compose. Perfect for journaling, note-taking, task management, and any application requiring rich text editing with media support.

## Features

### Rich Text Formatting
- **Text Styles**: Bold, italic, underline, strikethrough
- **Headings**: H1-H6 support
- **Lists**: Ordered and unordered lists with nesting
- **Code**: Inline code and syntax-highlighted code blocks
- **Links**: URL insertion and management
- **Colors**: Text and background color customization

### Content Blocks
- **Text Blocks**: Rich formatted text with paragraph styles
- **Media Blocks**: Images, videos, GIFs, and audio
- **Checklist Blocks**: Interactive todo lists with check/uncheck
- **File Attachments**: PDF and document support
- **Code Blocks**: Syntax highlighting for various languages
- **Dividers**: Visual separation with multiple styles

### Advanced Features
- **Auto-Save**: Automatic saving with configurable intervals (default: 3 seconds)
- **Undo/Redo**: Full history management with 100-state stack
- **Export**: Markdown, HTML, JSON, and plain text formats
- **Search**: Full-text search across all documents
- **Tags**: Organize documents with tags
- **Favorites**: Mark important documents
- **Word/Character Count**: Real-time statistics
- **Dark Mode**: Full theme support

## Architecture

The editor follows a clean, modular architecture with clear separation of concerns:

```
editor/
├── api/                    # Public API and contracts
│   ├── model/              # Data models
│   ├── repository/         # Repository interfaces
│   └── export/             # Export interfaces
└── impl/                   # Implementation
    ├── data/               # Data layer
    │   ├── database/       # Room database
    │   ├── repository/     # Repository implementation
    │   └── mappers/        # Entity ↔ Domain mapping
    ├── formatting/         # Text formatting engine
    ├── export/             # Export implementations
    └── ui/                 # Compose UI
        ├── screen/         # Main editor screen
        └── components/     # Reusable components
```

## Installation

### 1. Add Module Dependencies

In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":editor:api"))
    implementation(project(":editor:impl"))
}
```

### 2. Configure Hilt

Ensure your app module has Hilt configured:

```kotlin
plugins {
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
}
```

### 3. Initialize in Application Class

```kotlin
@HiltAndroidApp
class YourApplication : Application()
```

## Usage

### Basic Integration

```kotlin
import com.letsgotoperfection.editor.impl.ui.screen.EditorScreen

@Composable
fun YourScreen(navController: NavController) {
    EditorScreen(
        documentId = "optional-existing-document-id",
        onNavigateBack = { navController.popBackStack() }
    )
}
```

### Creating a New Document

```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import com.letsgotoperfection.editor.impl.ui.screen.EditorViewModel

@Composable
fun NewDocumentScreen() {
    val viewModel: EditorViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.createNewDocument()
    }

    EditorScreen(
        viewModel = viewModel,
        onNavigateBack = { /* handle back */ }
    )
}
```

### Loading an Existing Document

```kotlin
@Composable
fun EditDocumentScreen(documentId: String) {
    EditorScreen(
        documentId = documentId,
        onNavigateBack = { /* handle back */ }
    )
}
```

### Using the Repository

```kotlin
import com.letsgotoperfection.editor.api.repository.EditorRepository
import javax.inject.Inject

class YourViewModel @Inject constructor(
    private val editorRepository: EditorRepository
) : ViewModel() {

    fun getAllDocuments() = viewModelScope.launch {
        val documents = editorRepository.getAllDocuments()
        // Handle documents
    }

    fun observeDocument(id: String) {
        editorRepository.observeDocument(id)
            .collect { document ->
                // Handle document updates
            }
    }

    fun searchDocuments(query: String) = viewModelScope.launch {
        val results = editorRepository.searchDocuments(query)
        // Handle search results
    }
}
```

### Exporting Documents

```kotlin
import com.letsgotoperfection.editor.api.export.DocumentExporter
import com.letsgotoperfection.editor.api.export.ExportFormat
import javax.inject.Inject

class ExportManager @Inject constructor(
    private val exporter: DocumentExporter,
    private val repository: EditorRepository
) {
    suspend fun exportToMarkdown(documentId: String): String {
        val document = repository.getDocument(documentId) ?: return ""
        return exporter.export(document, ExportFormat.MARKDOWN)
            .getOrThrow()
    }

    suspend fun exportToHtml(documentId: String): String {
        val document = repository.getDocument(documentId) ?: return ""
        return exporter.export(document, ExportFormat.HTML)
            .getOrThrow()
    }
}
```

## Data Models

### EditorDocument

```kotlin
data class EditorDocument(
    val id: String,
    val title: String,
    val blocks: List<ContentBlock>,
    val metadata: DocumentMetadata,
    val createdAt: Long,
    val updatedAt: Long
)
```

### ContentBlock Types

```kotlin
// Text with rich formatting
data class TextBlock(
    override val id: String,
    val text: AnnotatedString,
    val style: TextBlockStyle
) : ContentBlock()

// Images, videos, audio
data class MediaBlock(
    override val id: String,
    val mediaType: MediaType,
    val uri: String,
    val metadata: MediaMetadata
) : ContentBlock()

// Interactive todo lists
data class ChecklistBlock(
    override val id: String,
    val items: List<ChecklistItem>,
    val title: String?
) : ContentBlock()

// File attachments
data class FileAttachmentBlock(
    override val id: String,
    val fileName: String,
    val fileUri: String,
    val fileSize: Long,
    val mimeType: String
) : ContentBlock()

// Code blocks
data class CodeBlock(
    override val id: String,
    val code: String,
    val language: String?
) : ContentBlock()

// Visual dividers
data class DividerBlock(
    override val id: String,
    val style: DividerStyle
) : ContentBlock()
```

## Customization

### Theming

The editor automatically adapts to your app's Material3 theme:

```kotlin
MaterialTheme(
    colorScheme = yourColorScheme,
    typography = yourTypography
) {
    EditorScreen(...)
}
```

### Custom Block Types

To add custom block types, extend `ContentBlock`:

```kotlin
data class CustomBlock(
    override val id: String,
    override val position: Int,
    override val createdAt: Long,
    override val updatedAt: Long,
    val customData: String
) : ContentBlock()
```

## Performance

- **Lazy Loading**: Content blocks are rendered using `LazyColumn` for efficient memory usage
- **Auto-Save Debouncing**: Changes are debounced for 3 seconds to avoid excessive writes
- **Database Indexing**: Optimized Room queries with proper indices
- **State Management**: Efficient state updates using Kotlin Flow

## Testing

Run tests:

```bash
# Unit tests
./gradlew :editor:api:test
./gradlew :editor:impl:test

# Instrumented tests
./gradlew :editor:impl:connectedAndroidTest

# All tests
./gradlew :editor:test
```

## Requirements

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Kotlin**: 2.2.20+
- **Compose**: BOM 2025.10.00+
- **Java**: 11

## Dependencies

- Jetpack Compose
- Room Database
- Hilt (Dependency Injection)
- Kotlin Coroutines
- Coil (Image Loading)
- CommonMark (Markdown Parsing)
- Kotlinx Serialization

## Inspiration

This editor draws inspiration from world-class journaling and note-taking apps:
- **Day One**: Beautiful design and media handling
- **Joplin**: Open-source, markdown support, and extensibility
- **Notion**: Block-based editing and flexibility

## License

This module is part of the Systems project.

## Contributing

Contributions are welcome! Please ensure:
1. All tests pass
2. Lint checks pass
3. Code follows existing style
4. Documentation is updated

## Support

For issues and questions:
- Check existing documentation
- Review the codebase examples
- Create an issue with detailed information

---

**Built with ❤️ using Jetpack Compose**

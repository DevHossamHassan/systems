# Editor Module Architecture

## Overview

The editor module follows a clean, layered architecture with strict separation between API contracts and implementation details, enabling modularity and reusability across projects.

## Module Structure

```
:editor (parent module)
├── :editor:api (public contracts)
└── :editor:impl (implementation)
```

### Why API/Implementation Separation?

1. **Modularity**: Other modules depend only on the API, not the implementation
2. **Flexibility**: Implementation can be swapped without affecting consumers
3. **Testing**: Easy to mock the API for testing
4. **Encapsulation**: Internal details are hidden from consumers

## Layer Architecture

```
┌─────────────────────────────────────────────┐
│          Presentation Layer (UI)            │
│  • EditorScreen (Composable)                │
│  • EditorViewModel (State Management)       │
│  • Block Renderers (Text, Media, etc.)      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          Domain Layer (API)                 │
│  • EditorDocument (Model)                   │
│  • ContentBlock (Sealed Class)              │
│  • EditorRepository (Interface)             │
│  • DocumentExporter (Interface)             │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          Data Layer (Implementation)        │
│  • Room Database                            │
│  • EditorRepositoryImpl                     │
│  • Entity Mappers                           │
│  • DocumentExporterImpl                     │
└─────────────────────────────────────────────┘
```

## Data Flow

### Creating a Document

```
User Input
    ↓
EditorScreen (Composable)
    ↓
EditorViewModel.createNewDocument()
    ↓
EditorRepository.createDocument()
    ↓
EditorRepositoryImpl (maps domain → entity)
    ↓
Room Database (persists)
    ↓
Flow emissions (propagates changes)
    ↓
UI updates automatically
```

### Reading Documents

```
EditorScreen LaunchedEffect
    ↓
EditorViewModel.loadDocument(id)
    ↓
EditorRepository.observeDocument(id)
    ↓
Room DAO observeDocument() → Flow
    ↓
Map: DocumentWithBlocks → EditorDocument
    ↓
StateFlow in ViewModel
    ↓
EditorScreen collectAsState()
    ↓
UI renders blocks
```

## Key Design Patterns

### 1. Repository Pattern

**Interface** (`editor:api`):
```kotlin
interface EditorRepository {
    suspend fun getDocument(id: String): EditorDocument?
    suspend fun createDocument(doc: EditorDocument): Result<EditorDocument>
    // ... more operations
}
```

**Implementation** (`editor:impl`):
```kotlin
class EditorRepositoryImpl @Inject constructor(
    private val dao: DocumentDao
) : EditorRepository {
    override suspend fun getDocument(id: String) =
        dao.getDocumentWithBlocks(id)?.toEditorDocument()
}
```

**Benefits**:
- Testable (mock the interface)
- Swappable implementations (e.g., cloud vs local)
- Clear contracts

### 2. Sealed Class Hierarchy

```kotlin
sealed class ContentBlock {
    abstract val id: String
    abstract val position: Int
}

data class TextBlock(...) : ContentBlock()
data class MediaBlock(...) : ContentBlock()
data class ChecklistBlock(...) : ContentBlock()
```

**Benefits**:
- Type-safe when blocks
- Exhaustive pattern matching
- Extensible for new block types

### 3. State Management (MVI-inspired)

```kotlin
data class EditorState(
    val document: EditorDocument,
    val focusedBlockId: String?,
    val showFormattingToolbar: Boolean
)

// ViewModel
private val _state = MutableStateFlow(EditorState())
val state: StateFlow<EditorState> = _state.asStateFlow()
```

**Benefits**:
- Predictable state changes
- Single source of truth
- Easy debugging (state snapshots)

### 4. Dependency Injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object EditorModule {
    @Provides
    @Singleton
    fun provideEditorRepository(dao: DocumentDao): EditorRepository =
        EditorRepositoryImpl(dao)
}
```

**Benefits**:
- Loose coupling
- Easy testing (inject mocks)
- Lifecycle management

### 5. Mapper Pattern

```kotlin
// Entity → Domain
fun DocumentWithBlocks.toEditorDocument(): EditorDocument

// Domain → Entity
fun EditorDocument.toDocumentEntity(): DocumentEntity
fun EditorDocument.toBlockEntities(): List<ContentBlockEntity>
```

**Benefits**:
- Clean separation of concerns
- Reusable transformations
- Type safety

## Database Design

### Schema

```sql
documents
├── id (PK)
├── title
├── metadata (JSON)
├── created_at
└── updated_at

content_blocks
├── id (PK)
├── document_id (FK → documents.id, CASCADE DELETE)
├── block_type (TEXT, MEDIA, CHECKLIST, etc.)
├── content (JSON)
├── position (for ordering)
├── created_at
└── updated_at
```

### Relationships

```
Document (1) ───< (Many) ContentBlocks
```

When a document is deleted, all its blocks are automatically deleted via `ON DELETE CASCADE`.

### Indexing

- `document_id` in `content_blocks` (for fast lookups)
- `position` in `content_blocks` (for ordered retrieval)

## Formatting System

### Text Formatting

```kotlin
TextFormatter
├── applyBold(text, start, end)
├── applyItalic(text, start, end)
├── applyUnderline(text, start, end)
├── applyTextColor(text, start, end, color)
└── toggleStyle(text, start, end, style)
```

Uses Compose's `AnnotatedString` with `SpanStyle` for rich text.

### Undo/Redo

```kotlin
UndoRedoManager<EditorDocument>
├── undoStack: Stack<EditorDocument>
├── redoStack: Stack<EditorDocument>
├── addState(state)
├── undo(currentState) → previousState
└── redo(currentState) → nextState
```

Maintains up to 100 document states in memory.

## Export System

```kotlin
interface DocumentExporter {
    suspend fun export(doc: EditorDocument, format: ExportFormat): Result<String>
}

enum class ExportFormat {
    MARKDOWN, HTML, JSON, PLAIN_TEXT, PDF
}
```

Each exporter transforms the block tree into the target format:

- **Markdown**: Standard GitHub-flavored Markdown
- **HTML**: Standalone HTML with inline CSS
- **JSON**: Serialized document structure
- **Plain Text**: Strips all formatting

## Performance Optimizations

### 1. Lazy Loading

```kotlin
LazyColumn {
    items(
        items = document.blocks,
        key = { it.id }
    ) { block ->
        BlockRenderer(block)
    }
}
```

Only renders visible blocks.

### 2. Debounced Auto-Save

```kotlin
state.map { it.document }
    .distinctUntilChanged()
    .debounce(3000)  // Wait 3s after last change
    .collect { saveDocument() }
```

Avoids excessive database writes.

### 3. Flow-based Updates

```kotlin
fun observeDocument(id: String): Flow<EditorDocument>
```

Room emits updates automatically when data changes.

### 4. Efficient Mappers

Mappers use `kotlinx.serialization` for fast JSON encoding/decoding of block content.

## Testing Strategy

### Unit Tests

- **Models**: Test helper methods (e.g., `addBlock`, `wordCount`)
- **Formatters**: Test text transformations
- **Mappers**: Test entity ↔ domain conversions
- **Repository**: Mock DAO, test logic

### Integration Tests

- **Database**: Test Room DAOs with in-memory database
- **ViewModel**: Test state transitions

### UI Tests

- **EditorScreen**: Test user interactions
- **Block Renderers**: Test rendering different block types

## Extending the Editor

### Adding a New Block Type

1. **Define model** in `editor:api/model`:
```kotlin
data class TableBlock(
    override val id: String,
    override val position: Int,
    override val createdAt: Long,
    override val updatedAt: Long,
    val rows: Int,
    val columns: Int,
    val cells: List<List<String>>
) : ContentBlock()
```

2. **Add JSON model** in `editor:impl/data/mappers`:
```kotlin
@Serializable
data class TableBlockJson(
    val rows: Int,
    val columns: Int,
    val cells: List<List<String>>
)
```

3. **Update mapper**:
```kotlin
when (blockType) {
    "TABLE" -> {
        val data = json.decodeFromString<TableBlockJson>(content)
        TableBlock(...)
    }
}
```

4. **Create UI renderer**:
```kotlin
@Composable
fun TableBlockView(block: TableBlock, ...)
```

5. **Add to BlockRenderer** in `EditorScreen`.

### Adding a New Export Format

1. **Add enum value**:
```kotlin
enum class ExportFormat {
    // ...
    CSV
}
```

2. **Implement exporter**:
```kotlin
private fun exportToCsv(document: EditorDocument): String {
    // Export logic
}
```

3. **Wire in `export()` method**.

## Security Considerations

1. **SQL Injection**: Prevented by Room's parameterized queries
2. **XSS in exports**: HTML export escapes user input
3. **File permissions**: Media files stored in app-private directory
4. **Data validation**: Input sanitization in formatters

## Future Enhancements

- [ ] Collaborative editing (OT/CRDT)
- [ ] Cloud sync
- [ ] Version history with diffs
- [ ] Custom themes
- [ ] Plugin system
- [ ] Table support
- [ ] Drawing/sketching blocks
- [ ] Voice memo blocks
- [ ] End-to-end encryption

---

This architecture provides a solid foundation for a world-class editor that can scale with your app's needs.

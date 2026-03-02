# Data Models

## EditorDocument

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

## ContentBlock Types

- **TextBlock** – Rich formatted text, TextBlockStyle (Body, Heading1–6, etc.)
- **MediaBlock** – mediaType, uri, metadata, thumbnailUri, caption
- **ChecklistBlock** – items (ChecklistItem), title
- **FileAttachmentBlock** – fileName, fileUri, fileSize, mimeType, thumbnailUri
- **CodeBlock** – code, language
- **DividerBlock** – style (SOLID, etc.)

## DocumentMetadata

- tags: List<String>
- favorite: Boolean

## Block Base

All blocks have: id, position, createdAt, updatedAt.

## Database Schema (Room – Android)

- **documents**: id, title, metadata (JSON), created_at, updated_at
- **content_blocks**: id, document_id (FK), block_type, content (JSON), position, created_at, updated_at
- ON DELETE CASCADE from documents to blocks

## Web Equivalents

Same conceptual model; map to chosen ORM/DB (e.g. PostgreSQL + TypeORM, Prisma, Drizzle).

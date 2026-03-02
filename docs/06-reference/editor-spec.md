# Editor Spec (Condensed)

*Condensed from [editor/README.md](../../editor/README.md) and [editor/ARCHITECTURE.md](../../editor/ARCHITECTURE.md)*

## Features

### Rich Text Formatting

- Text styles: Bold, italic, underline, strikethrough
- Headings: H1–H6
- Lists: Ordered, unordered, nesting
- Code: Inline, syntax-highlighted blocks
- Links, text/background colors

### Content Blocks

- Text, Media, Checklist, FileAttachment, Code, Divider
- Media: images, videos, GIFs, audio
- Checklists: interactive todo with check/uncheck

### Advanced

- Auto-save (configurable, default 3s)
- Undo/Redo (100-state stack)
- Export: Markdown, HTML, JSON, plain text
- Search, tags, favorites, word/character count
- Dark mode

## Architecture

- **API/Impl separation**: editor:api (contracts), editor:impl (implementation)
- **Layers**: Presentation (UI, ViewModel) → Domain (models, repository interface) → Data (Room, DAO)
- **Repository pattern**: EditorRepository interface, EditorRepositoryImpl
- **Block model**: Sealed ContentBlock hierarchy

## Data Flow

- Create: User → EditorScreen → ViewModel → Repository → Room → Flow → UI
- Read: LaunchedEffect → ViewModel.loadDocument → Repository.observeDocument → Room Flow → StateFlow → UI

## Future Enhancements (from ARCHITECTURE)

- Collaborative editing (OT/CRDT)
- Cloud sync
- Version history with diffs
- Table support
- Drawing/sketching blocks
- Voice memo blocks
- End-to-end encryption

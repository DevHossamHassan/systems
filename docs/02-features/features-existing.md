# Existing Features

Features implemented in the Android app (as of plan creation).

## Editor

- **Block types**: Text, Media, Checklist, FileAttachment, Code, Divider
- **Formatting**: Bold, italic, underline, strikethrough; headings H1–H6; lists (ordered/unordered); inline code; links; text/background colors
- **Auto-save**: Configurable interval (default 3 seconds)
- **Undo/Redo**: 100-state stack
- **Export**: Markdown, HTML, JSON, plain text
- **Search**: Full-text search across documents
- **Metadata**: Tags, favorites
- **Word/Character Count**: Real-time statistics
- **Persistence**: Room database

## Journal

- **UI**: List of entries, FAB to create
- **Entry cards**: Title + preview of first block
- **Create flow**: Opens editor for new document
- **Edit flow**: Opens editor for existing document by ID
- **Sample entries**: In-memory demo data (no persistence yet)

## Navigation

- Home → Journal → Editor
- Routes: `home`, `journal`, `editor/{documentId}` (including `editor/new`)

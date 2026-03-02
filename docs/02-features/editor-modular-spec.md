# Editor Modular Spec

## Overview

Modular, reusable rich editor. Confluence/MarkText parity: live preview, full MD, media, checklists. Used in Notes, Journal, People, and experience entries.

## Requirements

- **Live preview** – Split view (edit left, preview right) like Confluence or MarkText
- **Full Markdown support** – Headings, lists, code, links, etc.
- **Media with preview** – Images, videos, audio
- **Checklists** – Interactive todo items
- **Modular reuse** – Same editor component across Notes, Journal, People, experience entries, other apps

## Block Types (from Android editor)

- Text (rich formatting)
- Media (images, videos, audio)
- Checklist
- File attachment
- Code block
- Divider

## Recommended Libraries (Web)

- **Tiptap** – ProseMirror-based, collaboration (Hocuspocus), modular extensions
- **Lexical** – Meta, lightweight, React-first

## Architecture

- API/Implementation separation for modularity
- Domain models: EditorDocument, ContentBlock (sealed)
- Export: Markdown, HTML, JSON, plain text

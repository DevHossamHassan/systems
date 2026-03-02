# Module Mapping: Android → Web

## Android Modules

| Module | Purpose |
|--------|---------|
| app | Main application, navigation, theme |
| core | Shared utilities, base classes |
| systems (api/impl) | API/Implementation pattern example |
| people | People/contacts management |
| journal | Journaling |
| settings | App settings |
| editor (api/impl) | Rich text editing |
| media | Media handling |
| notifications | Notifications |

## Suggested Web Equivalents

| Android | Web (Monorepo) |
|---------|----------------|
| app | `apps/web` (Next.js, React SPA, etc.) |
| core | `packages/core` (shared utils) |
| editor | `packages/editor` (Tiptap/Lexical wrapper) |
| people | `packages/people` or feature in `apps/web` |
| journal | `packages/journal` or feature in `apps/web` |
| systems/experiences | `packages/feed` or `packages/experiences` |
| media | `packages/media` |
| notifications | `packages/notifications` or service |
| settings | Feature in `apps/web` |
| auth | `packages/auth` (new for web) |

## Shared Packages

- **editor** – Reusable across Notes, Journal, People, Experiences
- **auth** – Used by all authenticated features
- **core** – Utilities, types, constants

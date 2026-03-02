# Project Scope

## In Scope

- **Systems/Experiences Feed** – Searchable feed of community or personal entries (guides, packing lists, checklists). Visibility: Personal / Unlisted / Public per entry.
- **Notes** – Dedicated notes tab using shared editor. Searchability, structure, distill (never junk drawer).
- **Journal** – Journal entries using shared editor. 10-min sessions, prompts, morning/evening templates, brain dump.
- **People** – Conversation notes, BD logging + optional notifications, work context (title, background, last convo).
- **Media** – Dedicated tab: all media, categorized, sorted, searchable.
- **Editor** – Modular, Confluence/MarkText-style (live preview, full MD, media, checklists). Reusable in Notes, Journal, People, experience entries.
- **Auth** – Registration, login, email verification, forgot/change password.
- **Web platform** – Enterprise-grade quality.

## Out of Scope

- Mobile app rebuild. Web is the new platform; Android app exists separately.
- Native mobile clients (iOS/Android) for the web app handoff.

## Assumptions

- Users will have modern browsers with JavaScript enabled.
- Auth is required for all user-generated content.
- Localization: Arabic + English support per project rules.
- Editor is the foundational component; all content features depend on it.

## Constraints

- Enterprise-grade quality: security, scalability, observability.
- No hardcoded strings; all user-facing text must be localized.
- Follow modular architecture; editor must be reusable across features.

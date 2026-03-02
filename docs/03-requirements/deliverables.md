# Deliverables

## Phase 1: Foundation

- Auth: Registration, login, email verification, forgot/change password
- Core infra: DB, API, monorepo

## Phase 2: Editor First

- Editor module (Tiptap or Lexical)
- Live preview + full MD
- Blocks: text, media, checklist, code

## Phase 3: Content + Search

- Notes + Journal using editor
- Meilisearch for search
- Systems feed: create, list, search

## Phase 4: Social + People

- Votes/likes + ranking for Systems feed
- People: contacts, notes, BD
- BD notifications

## Phase 5: Media + Polish

- Media tab: categorized, searchable
- Settings, i18n, a11y
- Enterprise hardening

## Phase Dependencies

- **Editor** must land before Notes, Journal, People, Experiences (all depend on it)
- **Search** (Meilisearch) should be in place before Systems feed ranking
- **Auth** is first; everything else requires it
- **People** BD notifications need notification service (email/push)

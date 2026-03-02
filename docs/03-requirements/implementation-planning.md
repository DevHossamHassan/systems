# Implementation Planning

## Feature Assessment

### Editor

| Aspect | Current State | Improvement |
|--------|---------------|-------------|
| Live Preview | Not implemented | Add split-view live preview |
| State | Transaction-based | Ensure nested state, MD export |
| Collaboration | Future | Plan for OT/CRDT; Tiptap has Hocuspocus |

**Recommendation**: Tiptap (collab-ready, modular) or Lexical (lightweight)

### Systems/Experiences Feed

| Aspect | Current State | Improvement |
|--------|---------------|-------------|
| Feed Architecture | Not implemented | Event-driven: writes → events → materialized feeds |
| Votes/Likes | Not implemented | Immediate UI + async count updates; Redis for counts |
| Search | Not implemented | Meilisearch (typo-tolerant, simple) |

### People

| Aspect | Improvement |
|--------|-------------|
| Activity timeline | Per person (conversations, BDs, notes) |
| BD Notifications | Per-person toggle, job queue |
| Reconnection | Consider "Haven't contacted in 30 days" reminders |

### Auth (Web)

| Aspect | Best Practice |
|--------|---------------|
| Tokens | Short-lived access (5–15 min) + refresh tokens |
| Storage | HttpOnly cookies, not localStorage |
| Rotation | Refresh token rotation; revoke all if reuse detected |
| API Separation | Resource APIs never see refresh tokens |

### Architecture

| Aspect | Recommendation |
|--------|----------------|
| Modularity | Monorepo (Turborepo, Nx) |
| Feature Rollout | Feature flags + phased rollout |
| Post-launch | Usage analytics, onboarding |

## Tech Stack Recommendations

| Layer | Recommendation |
|-------|----------------|
| Editor | Tiptap or Lexical |
| Search | Meilisearch |
| Feed/Votes | Redis + Queue (Kafka/RabbitMQ or DB polling) |
| Auth | Custom JWT + refresh rotation, or Clerk |
| Monorepo | Turborepo or Nx |

## Phase Diagram

```
Phase 1 (Foundation) → Phase 2 (Editor) → Phase 3 (Content + Search)
    → Phase 4 (Social + People) → Phase 5 (Media + Polish)
```

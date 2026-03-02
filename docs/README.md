# Systems Documentation

Documentation handoff for building an **enterprise-grade web app** from the Systems Android productivity platform. Includes project idea, features, requirements, auth, and architecture.

---

## Quick Start

1. Read [Project Idea](01-overview/project-idea.md) for the core concept and feature overview.
2. Read [Project Scope](01-overview/project-scope.md) for in/out of scope.
3. Use [Implementation Planning](03-requirements/implementation-planning.md) for tech stack and phased roadmap.

---

## 01 – Overview

| Document | Description |
|----------|-------------|
| [project-idea.md](01-overview/project-idea.md) | Core concept: systems to reduce cognitive load. Hiking Egypt example. All features. |
| [systems-philosophy.md](01-overview/systems-philosophy.md) | Personal OS, checklists, Dr Justin Sung's 3 principles (holistic, repeatability, peel band-aids). |
| [project-scope.md](01-overview/project-scope.md) | In scope, out of scope, assumptions, constraints. |

---

## 02 – Features

| Document | Description |
|----------|-------------|
| [features-existing.md](02-features/features-existing.md) | Implemented in Android: Editor, Journal, Navigation. |
| [features-planned.md](02-features/features-planned.md) | Roadmap: Systems feed, Notes, People, Media, etc. |
| [feature-matrix.md](02-features/feature-matrix.md) | Summary table: Implemented vs Planned. |
| [systems-experiences-feed.md](02-features/systems-experiences-feed.md) | Searchable experiences, visibility, examples, votes, copy to my systems. |
| [people-feature.md](02-features/people-feature.md) | Conversation notes, BD, work context, editor integration. |
| [notes-feature.md](02-features/notes-feature.md) | Jacob Bennett insights: searchability, structure, distill. |
| [journal-feature.md](02-features/journal-feature.md) | Olly Jay: 10-min sessions, prompts, morning/evening, brain dump. |
| [editor-modular-spec.md](02-features/editor-modular-spec.md) | Confluence/MarkText style, live preview, MD, modular reuse. |

---

## 03 – Requirements

| Document | Description |
|----------|-------------|
| [user-stories.md](03-requirements/user-stories.md) | Inferred user stories (Systems, Notes, Journal, People, Auth, etc.). |
| [deliverables.md](03-requirements/deliverables.md) | Phase 1–5 deliverables and dependencies. |
| [implementation-planning.md](03-requirements/implementation-planning.md) | Feature assessment, tech stack, phased roadmap. |

---

## 04 – Auth & Web

| Document | Description |
|----------|-------------|
| [auth-requirements.md](04-auth-web/auth-requirements.md) | Registration, login, verification, forgot/change password. JWT, refresh tokens. |
| [enterprise-quality.md](04-auth-web/enterprise-quality.md) | Security, scalability, observability, testing, a11y, i18n. |

---

## 05 – Architecture

| Document | Description |
|----------|-------------|
| [data-models.md](05-architecture/data-models.md) | EditorDocument, ContentBlock types, DocumentMetadata. |
| [module-mapping.md](05-architecture/module-mapping.md) | Android modules → suggested web packages. |

---

## 06 – Reference

| Document | Description |
|----------|-------------|
| [editor-spec.md](06-reference/editor-spec.md) | Condensed editor spec from Android editor module. |
| [tech-stack-android.md](06-reference/tech-stack-android.md) | Original Android tech stack (for context). |

---

## 07 – Design System

| Document | Description |
|----------|-------------|
| [design-system.md](07-design-system/design-system.md) | Principles, branding, components. Normal vs high-end buttons; ripple from click point; shadow on hover. |
| [components.md](07-design-system/components.md) | Detailed component specs: Button, Card, TextField, IconButton. Implementation checklist. |

---

## Notes for Web Agent

- **Auth is new**: All auth flows are new for the web.
- **Editor is central**: Reused in Notes, Journal, People, experience entries.
- **Systems/Experiences Feed**: New concept; visibility Personal/Unlisted/Public; copy to my systems.
- **People**: Conversation notes, BD + notifications, work context.
- **Media tab**: Dedicated browser; categorized, sorted, searchable.
- **Localization**: Arabic + English support required.
- **Design system**: Use high-end button patterns (ripple from click point, shadow on hover). See [07-design-system](07-design-system/design-system.md).

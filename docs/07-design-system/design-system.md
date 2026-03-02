# Design System

Design system for Systems web app: consistency, branding, and component specifications. Enterprise-grade UI/UX with state-of-the-art interactions.

---

## Principles

- **Consistency** – Same patterns across features and screens
- **Feedback** – Every interaction has clear visual feedback
- **Accessibility** – WCAG-compliant; keyboard, screen reader, touch
- **Performance** – Smooth animations (60fps); no jank
- **Localization** – RTL-ready (Arabic), scalable layouts

---

## Branding

### Logo & Identity

- Primary logo for light/dark themes
- Favicon and app icons in required sizes
- Clear space and minimum size rules

### Color Palette

- **Primary** – Main brand color for CTAs, links
- **Secondary** – Supporting actions
- **Surface** – Backgrounds (light/dark variants)
- **Error, Warning, Success** – Semantic feedback
- **On-primary, On-surface** – Text on colored backgrounds

Use semantic tokens, not raw hex values. Support light and dark themes.

### Typography

- **Font family** – System stack or branded font; readable at all sizes
- **Scale** – Heading 1–6, body, caption, overline
- **Line height** – 1.2–1.5 depending on size
- **Weights** – Regular, medium, semi-bold, bold

### Spacing

- 4px base unit (or 8px)
- Consistent padding/margin scale (4, 8, 12, 16, 24, 32, 48)

---

## Component Specifications

### Buttons

#### Normal vs High-End Button

| Aspect | Normal | High-End |
|--------|--------|----------|
| **Ripple** | None or center-origin | **Origin at click/touch point** – ripple starts where user clicks |
| **Hover** | Background color change only | Background change + **shadow deepens** + slight lift |
| **Active/Press** | Simple color darken | Shadow reduces, button presses down (tactile feel) |
| **Touch** | No special handling | Ripple scales from **finger/touch point** |
| **Feedback** | Minimal | Clear, immediate, location-aware |

#### Ripple Effect (Material Design–style)

*Ref: [Mastering the Ripple Effect](https://medium.com/@designyff/mastering-the-ripple-effect-a-guide-to-building-engaging-ui-buttons-2bad2857d40d), [Material Design Ripple](https://m2.material.io/develop/web/supporting/ripple)*

- **Origin**: Ripple must start from the **exact click/touch coordinates**, not the center
- **Implementation**: Use `event.clientX`, `event.clientY` (or touch equivalent) relative to button bounds
- **Shape**: Circular (`border-radius: 50%`), expands outward
- **Animation**: Scale from 0 to ~4x over 400–600ms, opacity fades to 0
- **Container**: `position: relative`, `overflow: hidden` on button

#### Hover & Shadow

- **Rest**: Light shadow, e.g. `0 2px 4px rgba(0,0,0,0.07)`
- **Hover**: Shadow deepens, e.g. `0 4px 8px rgba(0,0,0,0.12)`; optional `translateY(-2px)` for lift
- **Active**: Shadow flattens; `translateY(0)` or slight press-down
- **Transition**: 120–300ms for smooth state changes

#### Button Variants

- **Primary** – Filled, highest emphasis
- **Secondary** – Outlined or tonal
- **Tertiary** – Text-only
- **Destructive** – Error/danger actions

#### Touch Targets

- Minimum 44×44px (WCAG 2.2)
- Adequate spacing between interactive elements

---

### Cards

- Resting elevation with light shadow
- Hover: shadow deepens, optional subtle scale
- Click: ripple from click point
- Rounded corners (e.g. 8px, 12px)
- Padding and content hierarchy

---

### Inputs (Text Fields)

- Clear focus state (outline, border color)
- Label floats or stays above
- Error and helper text styling
- Disabled state visually distinct

---

### Lists & Feed Items

- Hover state for rows/items
- Ripple on click (from touch point)
- Selected/active state
- Dividers or spacing for separation

---

## Elevation & Shadows

Use a small set of elevation levels:

| Level | Use | Shadow (example) |
|-------|-----|------------------|
| 0 | Flat, inline | None |
| 1 | Cards, buttons at rest | `0 1px 3px rgba(0,0,0,0.08)` |
| 2 | Hover, raised buttons | `0 4px 8px rgba(0,0,0,0.12)` |
| 3 | Modals, dropdowns | `0 8px 16px rgba(0,0,0,0.15)` |
| 4 | Overlays, dialogs | `0 12px 24px rgba(0,0,0,0.18)` |

Layered shadows (2–3 layers) give more natural depth than a single `box-shadow`.

---

## Motion & Animation

- **Duration**: 120–300ms for micro-interactions; 300–500ms for larger transitions
- **Easing**: `ease-out` or custom cubic-bezier for snappy feedback
- **Reduce motion**: Respect `prefers-reduced-motion` for accessibility

---

## Accessibility

- **Focus visible**: Clear focus ring (2px outline, high contrast)
- **Color contrast**: 4.5:1 minimum for body text; 3:1 for large text
- **contentDescription / aria-label**: All icons and images
- **Keyboard**: All interactive elements reachable and operable via keyboard
- **Touch targets**: Minimum 44×44px

---

## References

- [Mastering the Ripple Effect – Designyff (Medium)](https://medium.com/@designyff/mastering-the-ripple-effect-a-guide-to-building-engaging-ui-buttons-2bad2857d40d)
- [Material Design – Ripple](https://m2.material.io/develop/web/supporting/ripple)
- [Material Design – Buttons](https://m1.material.io/components/buttons.html)
- [Material Design – Elevation & Shadows](https://m1.material.io/material-design/elevation-shadows.html)
- [How to Create the Ripple Effect from Material Design – Jhey (Medium)](https://jh3y.medium.com/how-to-create-the-ripple-effect-from-google-material-design-c6f993e1d39)
- [CSS-Tricks – Ripple Effect](https://css-tricks.com/how-to-recreate-the-ripple-effect-of-material-design-buttons/)

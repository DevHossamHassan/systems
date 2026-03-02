# Component Library

Detailed component specs for the Systems design system. Use these for consistent implementation across the web app.

---

## Button

### Ripple (Click/Touch Origin)

```javascript
// Ripple must originate from click/touch point
function createRipple(event) {
  const button = event.currentTarget;
  const rect = button.getBoundingClientRect();
  const x = (event.clientX ?? event.touches?.[0]?.clientX) - rect.left;
  const y = (event.clientY ?? event.touches?.[0]?.clientY) - rect.top;
  const diameter = Math.max(button.clientWidth, button.clientHeight);
  const radius = diameter / 2;
  // Position ripple center at (x, y)
  // left = x - radius, top = y - radius
}
```

### States

| State | Shadow | Transform | Background |
|-------|--------|-----------|------------|
| Rest | elevation-1 | none | primary/default |
| Hover | elevation-2 (deeper) | translateY(-2px) | slightly darker |
| Active/Press | elevation-0 (flatter) | translateY(0) | darker |
| Focus | focus ring | same as rest | same |
| Disabled | none | none | muted, reduced opacity |

### Variants

- **Primary**: Filled, primary color
- **Secondary**: Outlined or tonal fill
- **Text**: No background, underline on hover
- **Destructive**: Error/danger color

---

## Card

- Padding: 16px or 24px
- Border radius: 8px or 12px
- Shadow: elevation-1 at rest; elevation-2 on hover
- Optional ripple on entire card click
- Min height for consistent lists

---

## Text Field

- Label: Above or floating
- Border: 1px solid, 2px on focus
- Padding: 12px 16px
- Error state: Red border, error message below
- Disabled: Reduced opacity, no pointer events

---

## Icon Button

- Min 44×44px touch target
- Ripple from touch point
- Icon size: 24px default
- Variants: Filled, outlined, tonal

---

## Checklist for Implementation

- [ ] Ripple originates from click/touch point (not center)
- [ ] Shadow deepens on hover
- [ ] Active state shows "press" feedback
- [ ] Touch targets ≥ 44px
- [ ] Focus visible
- [ ] Respect prefers-reduced-motion
- [ ] All strings localized

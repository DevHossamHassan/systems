# Auth Requirements (Web)

## Overview

The Android app has no auth. All auth flows are new for the web app. Enterprise-grade implementation required.

## Flows

### Registration

- Email + password
- Input validation
- Email verification required before full access

### Login

- Email + password
- Rate limiting on failed attempts

### Email Verification

- Send verification link on registration
- Resend verification option
- Verified status required for protected actions

### Forgot Password / Change Password

- Request reset link via email
- Token expiry (e.g. 1 hour)
- Secure password change flow
- Logout other sessions on password change (optional)

## Token Strategy

| Token | Lifespan | Storage |
|-------|----------|---------|
| Access token | 5–15 min | Memory or HttpOnly cookie |
| Refresh token | 7 days | HttpOnly cookie, `secure`, `sameSite=Strict` |

- **Refresh token rotation**: New refresh token on each refresh; revoke all if token reuse detected (possible theft)
- **Avoid localStorage** for tokens (XSS risk)

## API Separation

- Resource APIs validate **access tokens only**
- Refresh tokens handled by dedicated auth service/endpoint
- Resource APIs never see or validate refresh tokens

## Security

- Rate limiting (login attempts, password reset)
- CSRF protection
- Secure headers (CSP, HSTS, etc.)
- Password strength requirements
- No logging of passwords or tokens

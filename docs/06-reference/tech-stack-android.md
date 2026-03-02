# Tech Stack (Android – Original)

*For context when building the web app. See project root [README.md](../../README.md) and [CLAUDE.md](../../CLAUDE.md).*

## Core

- **Language**: Kotlin 2.0.20
- **Build**: Gradle, Kotlin DSL
- **Min SDK**: 26 (Android 8.0)
- **Target/Compile SDK**: 36
- **JVM Target**: Java 11

## UI

- Jetpack Compose (BOM 2024.09.03)
- Material3
- Navigation Compose 2.9.5

## Data & Async

- Room 2.6.1
- Kotlin Coroutines 1.8.1

## DI

- Hilt 2.52
- Hilt Navigation Compose 1.2.0

## Editor-Specific

- Coil (image loading)
- CommonMark (markdown parsing)
- Kotlinx Serialization

## Testing

- JUnit 4.13.2
- Espresso 3.7.0
- Turbine (Flow testing)

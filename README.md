# Systems

A modern Android productivity application built with Kotlin and Jetpack Compose, following a modular architecture pattern.

## Overview

Systems is designed as a comprehensive productivity platform with multiple integrated features including people management, journaling, media handling, and more. The app leverages the latest Android development best practices and tools.

## Architecture

### Multi-Module Structure

The project follows a clean, modular architecture that promotes separation of concerns and scalability:

```
systems/
├── app/              # Main application module
├── core/             # Shared utilities and base classes
├── systems/          # API/Implementation pattern example
│   ├── api/          # Public interfaces
│   └── impl/         # Implementation details
└── Feature Modules:
    ├── people/       # People/contacts management
    ├── journal/      # Journaling functionality
    ├── settings/     # App settings
    ├── editor/       # Text editing features
    ├── media/        # Media handling
    └── notifications/ # Notifications management
```

### Design Principles

- **Modular Design**: Each feature is isolated in its own module
- **API/Impl Pattern**: Demonstrated in the `:systems` module for loose coupling
- **Dependency Injection**: Hilt for clean dependency management
- **Modern UI**: 100% Jetpack Compose with Material3 design

## Tech Stack

### Core Technologies
- **Language**: Kotlin 2.2.20
- **UI Framework**: Jetpack Compose (BOM 2025.10.00)
- **Build System**: Gradle 8.13.0 with Kotlin DSL
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36

### Key Libraries
- **Dependency Injection**: Hilt 2.57.2
- **Navigation**: Navigation Compose 2.9.5
- **Database**: Room 2.8.2
- **Async**: Kotlin Coroutines 1.8.1
- **Testing**: JUnit 4.13.2, Espresso 3.7.0

## Getting Started

### Prerequisites

- **Android Studio**: Android Studio 2 or later (with Java 21 support)
- **JDK**: Java 21 (configured in `gradle.properties`)
- **Min SDK**: Android 8.0 (API 26) device or emulator

### Setup

1. Clone the repository:
```bash
git clone git@github.com:DevHossamHassan/systems.git
cd systems
```

2. Open the project in Android Studio

3. Sync Gradle dependencies:
```bash
./gradlew build
```

4. Run the app:
```bash
./gradlew installDebug
```

## Build Commands

### Development

```bash
# Build the project
./gradlew build

# Clean build
./gradlew clean build

# Install debug build on device
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

### Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :app:test
./gradlew :people:test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Code Quality

```bash
# Run lint checks
./gradlew lint

# Run lint on specific module
./gradlew :app:lint
```

## Module Development

### Adding a New Feature Module

1. Create the module directory structure
2. Add to `settings.gradle.kts`:
```kotlin
include(":feature-name")
```

3. Create `build.gradle.kts` with standard configuration:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.letsgotoperfection.feature_name"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}
```

4. Follow the package convention: `com.letsgotoperfection.<module_name>`

### Dependency Injection

When adding Hilt to a module:

```kotlin
plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

dependencies {
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    // For Compose navigation
    implementation(libs.hilt.navigation)
}
```

## Project Structure

### Package Naming Convention
- Base package: `com.letsgotoperfection`
- Module package: `com.letsgotoperfection.<module_name>`
- Source location: `src/main/java/com/letsgotoperfection/<module_name>/`

### Version Catalog

All dependencies are centralized in `gradle/libs.versions.toml` for consistent version management across modules.

## Development Guidelines

### Code Style
- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Leverage Material3 design guidelines
- Write testable code with dependency injection

### Testing
- Write unit tests for business logic
- Use instrumented tests for UI components
- Maintain test coverage for critical paths

### Git Workflow
1. Create feature branches from `main`
2. Make focused commits with clear messages
3. Test before pushing
4. Create pull requests for review

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[Add your license here]

## Contact

[Add your contact information]

---

Built with Kotlin and Jetpack Compose

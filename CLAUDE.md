# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application built with Kotlin and Jetpack Compose, following a modular architecture pattern. The app appears to be a productivity system with multiple feature modules.

## Build Commands

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run unit tests for a specific module
./gradlew :app:test
./gradlew :core:test
./gradlew :people:test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run instrumented tests for a specific module
./gradlew :app:connectedAndroidTest
```

### Running the App
```bash
# Install and run debug build
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

### Linting
```bash
# Run lint checks
./gradlew lint

# Run lint on specific module
./gradlew :app:lint
```

### Clean Build
```bash
./gradlew clean
```

## Module Architecture

The project uses a multi-module architecture organized as follows:

### Core Modules
- **`:app`** - Main application module containing `MainActivity`, navigation setup, and app theme
  - Uses Hilt for dependency injection
  - Uses Jetpack Compose for UI
  - Namespace: `com.letsgotoperfection.systems`

- **`:core`** - Shared core utilities and base classes
  - Namespace: `com.letsgotoperfection.core`
  - Contains common functionality used across feature modules

### Feature Modules
All feature modules follow the same structure and are independent Android library modules:

- **`:people`** - People/contacts management feature
- **`:journal`** - Journaling feature
- **`:settings`** - App settings feature
- **`:editor`** - Editor functionality
- **`:media`** - Media handling
- **`:notifications`** - Notifications management

### Systems Module (API/Implementation Pattern)
The `:systems` module follows an API/Implementation separation pattern:

- **`:systems`** - Parent module
- **`:systems:api`** - Public API interfaces (namespace: `com.letsgotoperfection.api`)
- **`:systems:impl`** - Implementation details (namespace: `com.letsgotoperfection.impl`)

This pattern allows for loose coupling between modules, where features can depend on the API without depending on the implementation.

## Technology Stack

### Build Configuration
- **Gradle**: Kotlin DSL (`.kts` files)
- **Android Gradle Plugin**: 8.13.0
- **Kotlin**: 2.2.20
- **Min SDK**: 26 (Android 8.0)
- **Target/Compile SDK**: 36
- **JVM Target**: Java 11

### Key Dependencies
Dependencies are centralized in `gradle/libs.versions.toml`:

- **UI Framework**: Jetpack Compose (BOM 2025.10.00)
- **Dependency Injection**: Hilt 2.57.2 (used in `:app` module)
- **Navigation**: Navigation Compose 2.9.5
- **Database**: Room 2.8.2 (declared but not yet used in modules)
- **Coroutines**: 1.8.1 (declared but not yet used in modules)
- **Testing**: JUnit 4.13.2, Espresso 3.7.0

### Compose Setup
The `:app` module has Compose enabled with:
- Kotlin Compose Compiler plugin
- Material3 design system
- Edge-to-edge display support

## Development Guidelines

### Adding a New Feature Module

1. Create module directory and add to `settings.gradle.kts`
2. Use the standard library module template with namespace `com.letsgotoperfection.<module_name>`
3. Set `minSdk = 26`, `compileSdk = 36`, `jvmTarget = "11"`
4. Add dependencies using version catalog references from `libs.versions.toml`

### Dependency Injection

The `:app` module uses Hilt (Dagger). When working with dependency injection:
- Apply both `ksp` and `hilt` plugins in module's `build.gradle.kts`
- Add `hilt` and `hilt-compiler` (via ksp) dependencies
- For Compose navigation, use `hilt-navigation` dependency

### Navigation

Navigation is centralized in the `:app` module:
- Main navigation setup: `app/src/main/java/com/letsgotoperfection/systems/navigation/AppNavigation.kt`
- Currently using Jetpack Navigation Compose

### Package Structure

All code follows the package naming convention:
- Base package: `com.letsgotoperfection`
- Module package: `com.letsgotoperfection.<module_name>`
- Source location: `src/main/java/com/letsgotoperfection/<module_name>/`

## Error Tracking System

This project uses a centralized error tracking database located at:
```
/Users/hossamhassan/StudioProjects/.project-knowledge/
```

### Before Investigating Errors

**Always search the error database first** before investigating issues:

```bash
cd /Users/hossamhassan/StudioProjects/.project-knowledge
python3 error_tracker.py similar "your error message"
```

This database contains previously solved errors and their working fixes across all projects.

### When You Fix an Error

Document it in the database so future instances can benefit:

```python
from error_tracker import ErrorTracker

with ErrorTracker() as tracker:
    error_id = tracker.add_error(
        project_name="Systems",
        error_type="build",  # build, runtime, dependency, configuration, etc.
        title="Brief description",
        error_message="The actual error",
        # ... other details
    )

    tracker.add_fix(
        error_id=error_id,
        description="How you fixed it",
        tested=True,
        verified=True,
        worked=True
    )
```

See `/Users/hossamhassan/StudioProjects/.project-knowledge/QUICK_REFERENCE.md` for details.

## Known Fixed Issues

The following errors have been encountered and fixed (documented in error database):

1. **Navigation compilation issue** - Empty NavHost implementation
2. **Java version mismatch** - AGP 8.13.0 requiring Java 17+ (configured to use Android Studio 2's Java 21)
3. **Hilt Navigation dependency** - Wrong group (should be `androidx.hilt` not `com.google.dagger`)

## Notes

- The project is in early stages with mostly empty feature modules containing only example test files
- Room and Coroutines are declared in the version catalog but not yet integrated into feature modules
- The `:systems` module demonstrates the API/Impl pattern that could be extended to other features for better modularity
- Gradle is configured to use Java 21 from Android Studio 2 (see `gradle.properties`)

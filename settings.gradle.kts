pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Systems"
include(":app")
include(":core")
include(":systems")
include(":people")
include(":journal")
include(":settings")
include(":editor")
include(":editor:api")
include(":editor:impl")
include(":media")
include(":notifications")
include(":systems:api")
include(":systems:impl")

pluginManagement {
    includeBuild("build-logic")
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

rootProject.name = "Vocabri"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":domain")
include(":data")
include(":core:logger")
include(":core:utils")

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

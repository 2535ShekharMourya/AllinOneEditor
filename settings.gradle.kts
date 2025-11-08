pluginManagement {
    plugins {
        id("com.google.dagger.hilt.android") version "2.48"
    }
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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
//        maven { url 'https://plugins.gradle.org/m2/' }
        // (optional) any other private repos you want globally
    }
}
rootProject.name = "CollageProject"
include(":app")
include(":stickers")

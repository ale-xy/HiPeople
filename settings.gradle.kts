pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-android/")
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/maven/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            setUrl("https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-android/")
        }
        maven {
            setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven/")
        }
    }
}
rootProject.name = "Hi People"

include(":app")
include(":core:testing")
include(":core:ui")
include(":feature:hostme")
include(":test-app")
include(":core:model")
include(":core:network")
include(":feature:auth")

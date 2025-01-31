pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Hi People"

include(":app")
include(":core:data")
include(":core:database")
include(":core:testing")
include(":core:ui")
include(":feature:hostme")
include(":test-app")
include(":core:model")
include(":core:network")

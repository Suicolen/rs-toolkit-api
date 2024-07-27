rootProject.name = "rs-toolkit-api"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.24"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("api")

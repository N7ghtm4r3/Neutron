rootProject.name = "Neutron"

pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.21"
        kotlin("multiplatform") version "2.1.21"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include("core")
include("backend")

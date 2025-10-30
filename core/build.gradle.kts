
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    alias(libs.plugins.androidLibrary)
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "2.0.20"
}

group = "com.tecknobit.neutroncore"
version = "1.0.4"

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            this@jvm.compilerOptions {
                jvmTarget.set(JvmTarget.JVM_18)
            }
        }
    }
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64()
    ).forEach { appleTarget ->
        appleTarget.binaries.framework {
            baseName = "neutron-core"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            webpackTask {
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(libs.equinox.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
            }
        }

    }

    jvmToolchain(18)
}

android {
    namespace = "com.tecknobit.neutroncore"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.neutroncore"
                artifactId = "neutroncore"
                version = "1.0.4"
                from(components["kotlin"])
            }
        }
    }
}
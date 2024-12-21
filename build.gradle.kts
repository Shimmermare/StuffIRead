@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "2.0.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
    id("org.jetbrains.compose")
    id("de.comahe.i18n4k") version "0.9.0"
}

group = "com.shimmermare.stuffiread"
version = "1.3.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${extra["kotlinx-serialization-json.version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${extra["kotlinx-datetime.version"]}")
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:${extra["compose.version"]}")
                implementation("com.godaddy.android.colorpicker:compose-color-picker:${extra["compose-color-picker.version"]}")
                implementation("io.github.aakira:napier:${extra["napier.version"]}")
                implementation("io.github.reactivecircus.cache4k:cache4k:${extra["cache4k.version"]}")
                implementation("com.russhwolf:multiplatform-settings:${extra["multiplatform-settings.version"]}")
                implementation("io.ktor:ktor-client-core:${extra["ktor-client.version"]}")
                implementation("io.ktor:ktor-client-cio:${extra["ktor-client.version"]}")
                implementation("de.comahe.i18n4k:i18n4k-core:${extra["i18n4k.version"]}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jsoup:jsoup:${extra["jsoup.version"]}")
                implementation("net.xyzsd.plurals:cldr-plural-rules:${extra["cldr-plural-rules.version"]}")
            }
        }

        val jvmTest by getting

        // Suppress unused warning
        listOf(commonMain, commonTest, jvmMain, jvmTest)
    }
}

compose.desktop {
    application {
        mainClass = "com.shimmermare.stuffiread.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = name
            packageVersion = version.toString()
            copyright = "Shimmermare 2023-2025"

            vendor = "Shimmermare"
            licenseFile.set(layout.projectDirectory.file("LICENSE.txt"))

            windows {
                shortcut = true
                iconFile.set(project.file("src/commonMain/resources/icons/icon.ico"))
            }
            linux {
                shortcut = true
                iconFile.set(project.file("src/commonMain/resources/icons/icon.png"))
            }
            macOS {
                iconFile.set(project.file("src/commonMain/resources/icons/icon.icns"))
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

i18n4k {
    packageName = "com.shimmermare.stuffiread.i18n"
    sourceCodeLocales = listOf("en", "ru")
}

tasks.register<Copy>("preparePackagedReleaseDistributionForCurrentOS") {
    dependsOn("check", "packageReleaseDistributionForCurrentOS")

    doFirst { println("Copying package files:") }

    from(
        // This fails to match binaries in GitHub actions for some reason
        // fileTree(layout.buildDirectory.dir("compose/binaries/main-release"))
        //    .apply { exclude("**/app/**") }
        //    .files
        // Specify package dirs explicitly:
        TargetFormat.values()
            .filter { it.outputDirName != "app" }
            .map { layout.buildDirectory.dir("compose/binaries/main-release/${it.outputDirName}") }
    ).apply {
        includeEmptyDirs = false
    }

    into(layout.projectDirectory.dir("binaries"))

    eachFile { println(path) }
}

tasks.register<Zip>("preparePortableReleaseDistributionForWindows") {
    dependsOn("check", "createReleaseDistributable")

    doFirst {
        if (!TargetFormat.Exe.isCompatibleWithCurrentOS) {
            throw IllegalStateException("This task can't be executed on non-Windows OS")
        }
        println("Zipping portable files:")
    }

    from(
        layout.buildDirectory.dir("compose/binaries/main-release/app/${project.name}"),
        layout.projectDirectory.file("LICENSE.txt"),
        layout.projectDirectory.file("README.md"),
        layout.projectDirectory.file("ATTRIBUTIONS.md"),
    ).apply {
        includeEmptyDirs = false
    }

    archiveFileName.set("${project.name}-${project.version}-Portable-Windows.zip")
    destinationDirectory.set(layout.projectDirectory.dir("binaries"))

    eachFile { println(path) }
}
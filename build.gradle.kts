import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.10"
    id("org.jetbrains.compose")
}

group = "com.shimmermare.stuffiread"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
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
            }
        }

        val commonTest by getting

        val jvmMain by getting {

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
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "StuffIRead"
            packageVersion = "1.0.0"

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
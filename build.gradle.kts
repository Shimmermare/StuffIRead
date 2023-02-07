import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
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
                implementation(compose.desktop.currentOs)
                // For now exclude this lib bc it's too big if code stripping is not used
                //implementation("org.jetbrains.compose.material:material-icons-extended-desktop:${extra["compose.version"]}")
                implementation("com.godaddy.android.colorpicker:compose-color-picker:${extra["compose-color-picker.version"]}")
                implementation("io.github.aakira:napier:${extra["napier.version"]}")
                implementation("com.squareup.sqldelight:sqlite-driver:${extra["sqldelight.version"]}")
                implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${extra["sqldelight.version"]}")
            }
            sqldelight {
                database("Database") {
                    packageName = "com.shimmermare.stuffiread.data"
                }
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
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "StuffIRead"
            packageVersion = "1.0.0"
        }
    }
}
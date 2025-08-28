import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.compose") version "1.6.0"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation(compose.materialIconsExtended)
    }

compose.desktop {
    application {
        mainClass = "org.example.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "OsmotrPS"
            packageVersion = "1.0.0"

            windows {
                menuGroup = "Осмотр ПС"
                upgradeUuid = "5a0b8d38-1b19-46d0-ba32-5a9a9a9a9a9a"
                iconFile.set(project.file("icon.ico"))
                perUserInstall = true
            }
        }
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("OsmotrPS-fat.jar")
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
            optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
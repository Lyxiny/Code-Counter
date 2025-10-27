plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.0"
}

group = "cem.lyxiny"
version = "1.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2025.2")
    type.set("CL")
    downloadSources.set(false)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    patchPluginXml {
        sinceBuild.set("252")
        untilBuild.set("252.*")
    }
}

tasks.named("buildSearchableOptions") {
    enabled = false
}
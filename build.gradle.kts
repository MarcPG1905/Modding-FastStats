@file:Suppress("AvoidDuplicateDependencies")

plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.loom)
}

group = "com.marcpg.faststats"
val artifact = "modding"
version = libs.versions.faststats.get()
description = "Wrapper for faststats-java that automatically works with the major modding platforms and versions."

extra["modId"] = "modding_faststats"
extra["modName"] = "Modding FastStats"
extra["modVersion"] = libs.versions.faststats.get()
extra["modDescription"] = "Analytics for developers - modified version with improvements for modding."
extra["modGitHubUrl"] = "https://github.com/MarcPG1905/Modding-FastStats"

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    mavenCentral()

    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net")
    maven("https://maven.neoforged.net/releases/")
    maven("https://repo.faststats.dev/releases")
}

val childJars = configurations.create("childJars") {
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}

dependencies {
    compileOnly(libs.faststats.core)
    compileOnly(libs.faststats.config)

    childJars(libs.faststats.core)
    childJars(libs.faststats.config)

    minecraft(libs.minecraft)

    compileOnly(libs.bundles.fabric)
    compileOnly(libs.bundles.forge)
    compileOnly(libs.bundles.neoforge)
}

tasks {
    build {
        dependsOn(jar)
    }
    withType<AbstractPublishToMaven> {
        dependsOn(jar)
    }
    jar {
        archiveBaseName = artifact
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(childJars)
        from(childJars.map(::zipTree)) {
            exclude("module-info.class")
            exclude("META-INF/versions/**/module-info.class")
        }
    }
    processResources {
        val properties = rootProject.extra.properties.mapValues { it.value.toString() }
        inputs.properties(properties)
        filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta")) {
            expand(properties)
        }
    }
}

publishing.publications.create<MavenPublication>("maven") {
    from(project.components["java"])

    groupId = project.group.toString()
    artifactId = artifact
    version = project.version.toString()
}

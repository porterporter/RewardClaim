@file:Suppress("UnstableApiUsage", "PropertyName")

import dev.deftu.gradle.utils.GameSide

plugins {
    java
    kotlin("jvm")
    id("dev.deftu.gradle.multiversion")
    id("dev.deftu.gradle.tools")
    id("dev.deftu.gradle.tools.resources")
    id("dev.deftu.gradle.tools.bloom")
    id("dev.deftu.gradle.tools.shadow")
    id("dev.deftu.gradle.tools.minecraft.loom")
}

loom {
    log4jConfigs.from(file("${projectDir}/log4j2.xml")) // doesn't work
}

toolkitLoomHelper {
    useDevAuth()

    disableRunConfigs(GameSide.SERVER)

    useMixinRefMap(modData.id)
    useTweaker("org.polyfrost.oneconfig.internal.legacy.OneConfigTweaker", GameSide.CLIENT)
    useForgeMixin(modData.id) // Configures the mixins if we are building for forge, useful for when we are dealing with cross-platform projects.
}

// Configures the output directory for when building from the `src/resources` directory.
sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

// Adds the Polyfrost maven repository so that we can get the libraries necessary to develop the mod.
repositories {
    maven("https://repo.polyfrost.org/releases")
}

// Configures the libraries/dependencies for your mod.
dependencies {
    // If we are building for legacy forge, includes the launch wrapper with `shade` as we configured earlier.
    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
}

tasks.fatJar {
    enabled = false
}

tasks.remapJar {
    inputFile = file("build/devlibs/RewardClaim-1.0.1+1.8.9-forge-dev.jar")
}
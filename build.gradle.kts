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
    log4jConfigs.from(file("${rootDir}/log4j2.xml")) // doesn't work
}

toolkitLoomHelper {
    useDevAuth()

    disableRunConfigs(GameSide.SERVER)

    useMixinRefMap(modData.id)
    useForgeMixin(modData.id) // Configures the mixins if we are building for forge, useful for when we are dealing with cross-platform projects.
}

// Configures the output directory for when building from the `src/resources` directory.
sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

tasks.fatJar {
    enabled = false
}

tasks.remapJar {
    // TODO: change this to something that works more flexibly
    inputFile = file("build/devlibs/${modData.name}-${modData.version}+1.8.9-forge-dev.jar")
}
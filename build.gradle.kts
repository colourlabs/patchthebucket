plugins {
    id("java")
    alias(libs.plugins.shadow)
    eclipse
}

group = "net.colourlabs.patchthebucket"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

sourceSets {
    create("api") {
        java { setSrcDirs(listOf("src/api/java")) }
    }
    create("testPlugin") {
        java { setSrcDirs(listOf("src/testPlugin/java")) }
        resources { setSrcDirs(listOf("src/testPlugin/resources")) }
    }
    create("demo") {
        java { setSrcDirs(listOf("src/demo/java")) }
        resources { setSrcDirs(listOf("src/demo/resources")) }
    }
}

sourceSets.named("main") {
    compileClasspath += sourceSets["api"].output
    runtimeClasspath += sourceSets["api"].output
}

sourceSets.named("demo") {
    compileClasspath += sourceSets["api"].output
}

dependencies {
    compileOnly(libs.spigot.api)

    implementation(libs.asm.core)
    implementation(libs.asm.tree)
    implementation(libs.asm.commons)
    implementation(libs.byte.buddy.agent)

    "apiCompileOnly"(libs.asm.tree)
    "testPluginCompileOnly"(libs.spigot.api)
    "demoCompileOnly"(libs.spigot.api)
    "demoCompileOnly"(libs.asm.tree)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

eclipse {
    classpath {
        plusConfigurations.add(configurations.getByName("apiCompileClasspath"))
        plusConfigurations.add(configurations.getByName("demoCompileClasspath"))
        plusConfigurations.add(configurations.getByName("testPluginCompileClasspath"))
    }

}

tasks.jar {
    manifest {
        attributes(
            "Can-Retransform-Classes" to "true",
            "Can-Redefine-Classes" to "true"
        )
    }
}

val apiJar by tasks.registering(Jar::class) {
    archiveFileName.set("patchthebucket-api-${project.version}.jar")
    from(sourceSets["api"].output)
    dependsOn(sourceSets["api"].classesTaskName)
}

val testPluginJar by tasks.registering(Jar::class) {
    archiveFileName.set("test-plugin-${project.version}.jar")
    from(sourceSets["testPlugin"].output)
    dependsOn(sourceSets["testPlugin"].classesTaskName)
}

val demoJar by tasks.registering(Jar::class) {
    archiveFileName.set("patchthebucket-demo-${project.version}.jar")
    from(sourceSets["demo"].output)
    dependsOn(sourceSets["demo"].classesTaskName)
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    from(sourceSets["api"].output)
    dependsOn(sourceSets["api"].classesTaskName)
    
    // ASM is NOT relocated, must be at org.objectweb.asm.* so consumer plugins
    // can use the same ASM types at runtime (loaded via dependency classloader).

    manifest {
        attributes(
            "Can-Retransform-Classes" to "true",
            "Can-Redefine-Classes" to "true"
        )
    }
}

tasks.build {
    dependsOn(tasks.shadowJar, apiJar, testPluginJar, demoJar)
}

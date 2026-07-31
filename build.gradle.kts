import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("java")
    alias(libs.plugins.shadow)
    `maven-publish`
    eclipse
}

group = "net.colourlabs.patchthebucket"
version = "1.3.0"

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

sourceSets.named("test") {
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

    testImplementation(libs.junit.jupiter)
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

tasks.named<ProcessResources>("processResources") {
    filesMatching("plugin.yml") {
        filter { line -> line.replace("\${version}", project.version.toString()) }
    }
}

tasks.named<ProcessResources>("processTestPluginResources") {
    filesMatching("plugin.yml") {
        filter { line -> line.replace("\${version}", project.version.toString()) }
    }
}

tasks.named<ProcessResources>("processDemoResources") {
    filesMatching("plugin.yml") {
        filter { line -> line.replace("\${version}", project.version.toString()) }
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

    relocate("net.bytebuddy", "net.colourlabs.patchthebucket.shaded.net.bytebuddy")

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

tasks.test {
    useJUnitPlatform()
}

val apiSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["api"].java)
}

publishing {
    publications {
        register("api", MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = "patchthebucket-api"
            version = project.version.toString()
            artifact(apiJar)
            artifact(apiSourcesJar)

            pom {
                withXml {
                    val dependency = asNode().appendNode("dependencies").appendNode("dependency")
                    dependency.appendNode("groupId", "org.ow2.asm")
                    dependency.appendNode("artifactId", "asm-tree")
                    dependency.appendNode("version", libs.versions.asm.get())
                    dependency.appendNode("scope", "provided")
                }
            }
        }
    }
}

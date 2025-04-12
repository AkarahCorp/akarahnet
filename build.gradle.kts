plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

val pluginPacksVer = "main-SNAPSHOT"
val actionPacksVer = "main-SNAPSHOT"

group = "dev.akarah"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    compileOnly("com.mojang:datafixerupper:8.0.16")
    compileOnly("com.github.AkarahCorp:plugin-packs:ddc6b363b1")
    compileOnly("com.github.AkarahCorp:actions:519a41a039")
}

runPaper.folia.registerTask().configure {
    minecraftVersion("1.21.4")

    downloadPlugins {
        url("https://jitpack.io/com/github/AkarahCorp/plugin-packs/ddc6b363b1/plugin-packs-ddc6b363b1.jar")
        url("https://jitpack.io/com/github/AkarahCorp/actions/519a41a039/actions-519a41a039.jar")
    }
}

tasks.runServer {
    minecraftVersion("1.21.4")

    downloadPlugins {
        url("https://jitpack.io/com/github/AkarahCorp/plugin-packs/ddc6b363b1/plugin-packs-ddc6b363b1.jar")
        url("https://jitpack.io/com/github/AkarahCorp/actions/519a41a039/actions-519a41a039.jar")

        modrinth("axiom-paper-plugin", "4.0.4+1.21.4")
    }
}

val targetJavaVersion = 21

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
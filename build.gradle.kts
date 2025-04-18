plugins {
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadowJar)
    java
}

var displayName = "Microstom"

group = "com.github.klainstom"
version = "8.0.0"

dependencies {
    implementation("net.minestom:minestom-snapshots:fb895cb899") {
        exclude("org.tinylog")
    }

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.17")
    // https://mvnrepository.com/artifact/org.slf4j/jul-to-slf4j
    implementation("org.slf4j:jul-to-slf4j:2.0.17")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.18")
    // https://mvnrepository.com/artifact/org.jline/jline-reader
    implementation("org.jline:jline-reader:3.29.0")
    // https://mvnrepository.com/artifact/org.jline/jline-terminal
    implementation("org.jline:jline-terminal:3.29.0")
    // https://mvnrepository.com/artifact/net.minecrell/terminalconsoleappender
    implementation("net.minecrell:terminalconsoleappender:1.3.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    blossom {
        replaceToken("&Name", displayName)
        replaceToken("&version", version)
    }

    processResources {
        expand(
            mapOf(
                "Name" to displayName,
                "version" to version
            )
        )
    }

    shadowJar {
        manifest {
            attributes("Main-Class" to "com.github.klainstom.microstom.Server")
        }
        archiveBaseName.set(displayName)
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        mergeServiceFiles()
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }
    register<JavaExec>("run") {
        mainClass = "com.github.klainstom.microstom.Server"
        classpath = sourceSets["main"].runtimeClasspath
    }
}
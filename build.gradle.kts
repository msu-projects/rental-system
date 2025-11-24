plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.istarvin"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25)) // or your target Java version
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.mysql:mysql-connector-j:9.5.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
    shadowJar {
        archiveFileName.set("rental-system.jar")
        manifest {
            attributes["Main-Class"] = "com.istarvin.SakilaApplication"
        }
        mergeServiceFiles()
    }
}

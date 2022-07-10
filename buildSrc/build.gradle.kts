plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(gradleApi())
    implementation("org.testcontainers:postgresql:1.16.3")
}
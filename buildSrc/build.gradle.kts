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
    implementation("org.flywaydb:flyway-core:8.5.1")
    runtimeOnly("org.postgresql:postgresql:42.3.3")
}
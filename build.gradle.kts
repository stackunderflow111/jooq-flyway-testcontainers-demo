import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Configuration

plugins {
    id("nu.studer.jooq") version "6.0.1"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // provides the "org.testcontainers.containers.PostgreSQLContainer" class for the testcontainers plugin
        classpath("org.testcontainers:postgresql:1.16.3")
    }
}

group = "io.github.stackunderflow111"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    jooqGenerator("org.postgresql:postgresql:42.3.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    configurations {
        create("main") {  // name of the jOOQ configuration
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

val postgresDatabase = gradle.sharedServices.registerIfAbsent("jooqDatabase", io.github.stackunderflow111.jooqflywaytestcontainersdemo.database.buildservices.PostgresContainerDatabase::class) {
    parameters {
        // docker image name, required
        imageName.set("postgres:13-alpine")
    }
}

val migrationFilesLocation = "src/main/resources/db/migration"

val flywayMigratedDatabase = gradle.sharedServices.registerIfAbsent("flywayMigratedDatabase", io.github.stackunderflow111.jooqflywaytestcontainersdemo.flyway.buildservices.FlywayMigratedDatabase::class) {
    parameters {
        database.set(postgresDatabase)
        migrationFilesLocations.value(listOf("filesystem:$migrationFilesLocation"))
    }
}

tasks.named<JooqGenerate>("generateJooq") {
    // Make the generateJooq task dependent on the migration scripts so we get proper build caching
    // (trigger a clean rebuild only when the files change)
    inputs.files(fileTree(migrationFilesLocation))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    // Let the task participate in incremental builds and build caching
    allInputsDeclared.set(true)
    outputs.cacheIf {
        true
    }

    usesService(flywayMigratedDatabase)

    doFirst {
        val database = flywayMigratedDatabase.get()
        // this is the same as val jooqConfiguration = jooqGenerate.jooqConfiguration,
        // but it's a private field, so I have to bypass the access restriction using reflection
        val jooqConfigurationField = JooqGenerate::class.java.getDeclaredField("jooqConfiguration")
        jooqConfigurationField.isAccessible = true
        val jooqConfiguration = jooqConfigurationField.get(this) as Configuration

        jooqConfiguration.jdbc.apply {
            this.url = database.jdbcUrl
            this.user = database.username
            this.password = database.password
        }
    }
}

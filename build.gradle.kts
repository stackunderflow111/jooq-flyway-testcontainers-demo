import nu.studer.gradle.jooq.JooqGenerate
import org.flywaydb.core.Flyway
import org.jooq.meta.jaxb.Configuration
import io.github.stackunderflow111.jooqflywaytestcontainersdemo.Database

plugins {
    id("nu.studer.jooq") version "6.0.1"
    id("org.flywaydb.flyway") version "8.5.5"
    id("io.github.stackunderflow111.jooqflywaytestcontainersdemo.testcontainers") apply false
    id("io.github.stackunderflow111.jooqflywaytestcontainersdemo.external") apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.postgresql:postgresql:42.3.3")
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
    jooqGenerator("org.postgresql:postgresql:42.3.6")
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

abstract class FlywayMigratedDatabase : BuildService<FlywayMigratedDatabase.Params> {
    interface Params : BuildServiceParameters {
        val database: Property<Database<Database.Parameters>>
        val migrationFilesLocations: ListProperty<String>
    }

    init {
        val migrationFileLocations = parameters.migrationFilesLocations.get()
        val fluentConfiguration = Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .locations(*migrationFileLocations.toTypedArray())
        val flyway = fluentConfiguration.load()
        flyway.migrate()
    }

    val jdbcUrl: String
        get() = parameters.database.get().jdbcUrl
    val username: String
        get() = parameters.database.get().username
    val password: String
        get() = parameters.database.get().password
}

val jooqProfile: String by project
val databasePluginName = "io.github.stackunderflow111.jooqflywaytestcontainersdemo.$jooqProfile"

apply(plugin = databasePluginName)

val postgresDatabase = gradle.sharedServices.registrations.getByName(
    "jooqDatabase").service as Provider<Database<Database.Parameters>>

val migrationFilesLocation = "src/main/resources/db/migration"

val flywayMigratedDatabase = gradle.sharedServices.registerIfAbsent("flywayMigratedDatabase", FlywayMigratedDatabase::class) {
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

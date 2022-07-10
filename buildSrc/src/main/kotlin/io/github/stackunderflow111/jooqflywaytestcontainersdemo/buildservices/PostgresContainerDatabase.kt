package io.github.stackunderflow111.jooqflywaytestcontainersdemo.buildservices

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildServiceParameters
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

abstract class PostgresContainerDatabase : Database<PostgresContainerDatabase.Params>, AutoCloseable {
  interface Params : BuildServiceParameters {

    val imageName: Property<String>
  }

  private val container: PostgreSQLContainer<*>

  init {
    val params = parameters
    val imageName = params.imageName.get()
    container = PostgreSQLContainer<Nothing>(DockerImageName.parse(imageName))
    logger.info("Starting a container with image '{}'", imageName)
    container.start()
    logger.info("Container started successfully with URL '{}'", container.jdbcUrl)
  }

  override fun close() {
    container.stop()
  }

  override val jdbcUrl: String
    get() = container.jdbcUrl
  override val username: String
    get() = container.username
  override val password: String
    get() = container.password

  companion object {

    private val logger = LoggerFactory.getLogger(PostgresContainerDatabase::class.java)
  }
}
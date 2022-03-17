package io.github.stackunderflow111.jooqflywaytestcontainersdemo.database.buildservices;

import io.github.stackunderflow111.jooqflywaytestcontainersdemo.database.Database;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildServiceParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class PostgresContainerDatabase implements Database<PostgresContainerDatabase.Params>, AutoCloseable {

    public interface Params extends BuildServiceParameters {
        Property<String> getImageName();
    }

    private static final Logger logger = LoggerFactory.getLogger(PostgresContainerDatabase.class);
    private final PostgreSQLContainer<?> container;

    public PostgresContainerDatabase() {
        Params params = getParameters();
        String imageName = params.getImageName().get();
        container = new PostgreSQLContainer<>(DockerImageName.parse(imageName));
        logger.info("Starting a container with image '{}'", imageName);
        container.start();
        logger.info("Container started successfully with URL '{}'", container.getJdbcUrl());
    }

    @Override
    public void close() {
        container.stop();
    }

    public String getJdbcUrl() {
        return container.getJdbcUrl();
    }

    public String getUsername() {
        return container.getUsername();
    }

    public String getPassword() {
        return container.getPassword();
    }
}

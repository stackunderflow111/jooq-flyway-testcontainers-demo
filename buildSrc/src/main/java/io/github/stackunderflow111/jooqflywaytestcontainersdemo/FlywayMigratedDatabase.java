package io.github.stackunderflow111.jooqflywaytestcontainersdemo;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import java.util.List;

public abstract class FlywayMigratedDatabase implements BuildService<FlywayMigratedDatabase.Params> {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public interface Params extends BuildServiceParameters {
        Property<String> getJdbcUrl();
        Property<String> getUsername();
        Property<String> getPassword();
        ListProperty<String> getMigrationFilesLocations();
    }

    public FlywayMigratedDatabase() {
        Params params = getParameters();
        jdbcUrl = params.getJdbcUrl().get();
        username = params.getUsername().get();
        password = params.getPassword().get();
        List<String> migrationFileLocations = params.getMigrationFilesLocations().get();
        FluentConfiguration fluentConfiguration = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations(migrationFileLocations.toArray(new String[0]));
        Flyway flyway = fluentConfiguration.load();
        flyway.migrate();
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

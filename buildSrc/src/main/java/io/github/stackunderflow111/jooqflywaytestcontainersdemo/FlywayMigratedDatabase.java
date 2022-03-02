package io.github.stackunderflow111.jooqflywaytestcontainersdemo;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import java.util.List;

public abstract class FlywayMigratedDatabase implements BuildService<FlywayMigratedDatabase.Params> {
    public interface Params extends BuildServiceParameters {
        Property<String> getJdbcUrl();
        Property<String> getUsername();
        Property<String> getPassword();
        ListProperty<String> getMigrationFilesLocations();
    }

    public FlywayMigratedDatabase() {
        List<String> migrationFileLocations = getParameters().getMigrationFilesLocations().get();
        FluentConfiguration fluentConfiguration = Flyway.configure()
                .dataSource(getJdbcUrl(), getUsername(), getPassword())
                .locations(migrationFileLocations.toArray(new String[0]));
        Flyway flyway = fluentConfiguration.load();
        flyway.migrate();
    }

    public String getJdbcUrl() {
        return getParameters().getJdbcUrl().get();
    }

    public String getUsername() {
        return getParameters().getUsername().get();
    }

    public String getPassword() {
        return getParameters().getPassword().get();
    }
}

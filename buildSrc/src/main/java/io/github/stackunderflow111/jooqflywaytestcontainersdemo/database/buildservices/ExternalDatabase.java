package io.github.stackunderflow111.jooqflywaytestcontainersdemo.database.buildservices;

import io.github.stackunderflow111.jooqflywaytestcontainersdemo.database.Database;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildServiceParameters;

abstract public class ExternalDatabase implements Database<ExternalDatabase.Params> {

    public interface Params extends BuildServiceParameters {
        Property<String> getJdbcUrl();

        Property<String> getUsername();

        Property<String> getPassword();
    }

    @Override
    public String getJdbcUrl() {
        return getParameters().getJdbcUrl().get();
    }

    @Override
    public String getUsername() {
        return getParameters().getUsername().get();
    }

    @Override
    public String getPassword() {
        return getParameters().getPassword().get();
    }

}

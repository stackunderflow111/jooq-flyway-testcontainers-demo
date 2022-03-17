package io.github.stackunderflow111.jooqflywaytestcontainersdemo.database;

import org.gradle.api.services.BuildServiceParameters;

public interface Database<P extends BuildServiceParameters> extends org.gradle.api.services.BuildService<P> {
    String getJdbcUrl();

    String getUsername();

    String getPassword();
}
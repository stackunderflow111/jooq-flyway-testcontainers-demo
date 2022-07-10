package io.github.stackunderflow111.jooqflywaytestcontainersdemo.buildservices

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildServiceParameters

abstract class ExternalDatabase : Database<ExternalDatabase.Params> {
  interface Params : BuildServiceParameters {

    fun getJdbcUrl(): Property<String>
    fun getUsername(): Property<String>
    fun getPassword(): Property<String>
  }

  override val jdbcUrl: String
    get() = parameters.getJdbcUrl().get()
  override val username: String
    get() = parameters.getUsername().get()
  override val password: String
    get() = parameters.getPassword().get()
}
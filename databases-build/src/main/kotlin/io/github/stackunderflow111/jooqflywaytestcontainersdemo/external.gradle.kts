package io.github.stackunderflow111.jooqflywaytestcontainersdemo

import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.registerIfAbsent

abstract class ExternalDatabase : Database<ExternalDatabase.Parameters> {
  interface Parameters : Database.Parameters {
    val jdbcUrl: Property<String>
    val username: Property<String>
    val password: Property<String>
  }

  override val jdbcUrl: String
    get() = parameters.jdbcUrl.get()
  override val username: String
    get() = parameters.username.get()
  override val password: String
    get() = parameters.password.get()
}

gradle.sharedServices.registerIfAbsent("jooqDatabase", ExternalDatabase::class) {
  parameters {
    jdbcUrl.set("jdbc:postgresql://localhost")
    username.set("postgres")
    password.set("postgres")
  }
}

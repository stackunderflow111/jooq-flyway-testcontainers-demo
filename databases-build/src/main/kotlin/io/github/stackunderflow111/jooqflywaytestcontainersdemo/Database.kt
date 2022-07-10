package io.github.stackunderflow111.jooqflywaytestcontainersdemo

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

interface Database<P : Database.Parameters> : BuildService<P> {
  interface Parameters : BuildServiceParameters

  val jdbcUrl: String
  val username: String
  val password: String
}
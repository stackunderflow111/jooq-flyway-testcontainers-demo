package io.github.stackunderflow111.jooqflywaytestcontainersdemo.buildservices

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

interface Database<P : BuildServiceParameters> : BuildService<P> {

  val jdbcUrl: String
  val username: String
  val password: String
}
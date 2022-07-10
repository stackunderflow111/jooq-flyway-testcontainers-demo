package io.github.stackunderflow111.jooqflywaytestcontainersdemo
import io.github.stackunderflow111.jooqflywaytestcontainersdemo.buildservices.PostgresContainerDatabase

gradle.sharedServices.registerIfAbsent("jooqDatabase", PostgresContainerDatabase::class) {
  parameters {
    // docker image name, required
    imageName.set("postgres:13-alpine")
  }
}

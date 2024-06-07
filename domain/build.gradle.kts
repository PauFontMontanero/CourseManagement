/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("buildlogic.java-library-conventions")
}

tasks.jar {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  from ("./src/main/resources") {
    include("META-INF/services/cat.fcardara.bandhub.domain.services.ModelCreator")
  }
}
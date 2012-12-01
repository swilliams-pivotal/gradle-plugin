package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.vertx.java.core.Vertx
import org.vertx.java.deploy.impl.VerticleManager


class StartVertx extends DefaultTask {

  @TaskAction
  def runVertx() {
    Vertx vertx = Vertx.newVertx()
    VerticleManager manager = new VerticleManager(vertx)
  }

}

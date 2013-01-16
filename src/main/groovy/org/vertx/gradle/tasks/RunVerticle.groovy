package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction


class RunVerticle extends AbstractVertxInstanceTask {

  RunVerticleExtension config

  @TaskAction
  def runVerticle() {

    initVerticleManager(config)
    def json = super.toJSON(config.json)

    if (config.worker) {
      container.deployWorkerVerticle(config.main, json, config.instances)
    }
    else {
      container.deployVerticle(config.main, json, config.instances)
    }

    waitForever()
  }

}

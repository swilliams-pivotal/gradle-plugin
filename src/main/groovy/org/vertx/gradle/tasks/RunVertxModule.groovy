package org.vertx.gradle.tasks

import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultJavaExecAction

//import org.vertx.java.core.Handler
//import org.vertx.java.core.json.JsonObject


class RunVertxModule extends AbstractVertxInstanceTask {

  RunVertxModuleExtension config

  @TaskAction
  def runVertxModule() {

    initVerticleManager(config)
    def json = super.toJSON(config.json)

    container.deployModule(config.name, json, config.instances)

    waitForever()
  }

}

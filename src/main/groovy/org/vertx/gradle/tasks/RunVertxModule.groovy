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

    try {
      load(config)

      def json = mapToJSON(config.json)

      container?.deployModule(config.name, json, config.instances)

//      waitFor(10000L)
//      waitFor(10L, TimeUnit.SECONDS)
//      waitForever()
      Thread.sleep(10000L) { container.undeployAll() }

    }
    catch (Throwable t) {
      t.printStackTrace()
    }
    finally {
      super.cleanup()
    }
  }

}

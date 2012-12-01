package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.vertx.java.core.Vertx
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.impl.VerticleManager


class RunVertxModule extends DefaultTask {

  private String deploymentModDir = 'build/tmp/mods-test'

  private int instances = 1

  private JsonObject config = new JsonObject()

  private FileCollection classpath

  @TaskAction
  def runVertx() {
    Vertx vertx = Vertx.newVertx()
    VerticleManager manager = new VerticleManager(vertx)
    manager.deploymentModDir = project.file(deploymentModDir)
    File modDir = new File(deploymentModDir)
    manager.deployMod("${project.modulename}-v${project.version}", config, instances, modDir, null)
  }

}

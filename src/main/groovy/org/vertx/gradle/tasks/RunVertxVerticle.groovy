package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

import org.vertx.java.core.Handler
import org.vertx.java.core.Vertx
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.impl.VerticleManager


class RunVertxVerticle extends DefaultTask {

  private boolean worker = false

  private String verticleName = "app.js"

  private String configFile = 'app.json'

  private int instances = 1

  private String includes

  private String defaultRepo = 'http://vertx.io/'

  private FileCollection classpath

  @TaskAction
  def runVertx() {

    Vertx vertx;
    if (project.extensions.vertx.port > -1) {
      vertx = Vertx.newVertx(project.extensions.vertx.port, project.extensions.vertx.hostname)
    }
    else {
      vertx = Vertx.newVertx()
    }

    VerticleManager manager = new VerticleManager(vertx, defaultRepo)

    JsonObject config = new JsonObject()
    URL[] urls = new URL[0]
    File currentModDir = project.file(project.extensions.vertx.modDir)

    DeploymentHandler doneHandler = new DeploymentHandler(1)

    manager.deployVerticle(worker, verticleName, config, urls, instances, currentModDir, includes, doneHandler)

    doneHandler.await();

//    try {
//      System.console().readLine()
//    }
//    catch (Throwable e) {
//    }
//    finally {
//      manager.undeploy(moduleName, new Handler<String>() {
//        @Override
//        public void handle(String event) {
//          println "undeployed: $event"
//        }
//      })
//    }

  }

  public void setWorker(boolean worker) {
    this.worker = worker
  }

  public boolean getWorker() {
    this.worker
  }

  public void setInstances(int instances) {
    this.instances = instances
  }

  public int getInstances() {
    this.instances
  }

  public void setVerticleName(String verticleName) {
    this.verticleName = verticleName
  }

  public String getVerticleName() {
    this.verticleName
  }

  public void setIncludes(String includes) {
    this.includes = includes
  }

  public String getIncludes() {
    this.includes
  }

  public void setDefaultRepo(String defaultRepo) {
    this.defaultRepo = defaultRepo
  }

  public String getDefaultRepo() {
    this.defaultRepo
  }

  public void setClasspath(FileCollection classpath) {
    this.classpath = classpath
  }

  public FileCollection getClasspath() {
    this.classpath
  }

}

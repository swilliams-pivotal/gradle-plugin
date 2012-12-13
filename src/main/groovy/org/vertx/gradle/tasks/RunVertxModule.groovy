package org.vertx.gradle.tasks

import java.util.concurrent.TimeUnit

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

import org.vertx.java.core.Handler
import org.vertx.java.core.Vertx
import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.impl.ParentLastURLClassLoader
// import org.vertx.java.deploy.impl.VerticleManager


class RunVertxModule extends DefaultTask {

  private long moduleDownloadTimeout = 10

  private int instances = 1

  private String moduleName = "${project.modulename}-v${project.version}"

  private String configFile = 'app.json'

  private String defaultRepo = 'vert-x.github.com'

  private FileCollection classpath

  @TaskAction
  def runVertx() {

    if (classpath == null || classpath.isEmpty()) {
      throw new InvalidUserDataException("You must assign the vert.x libraries to the provided configuration")
    }

    Set<File> files = classpath.getFiles()
    Set<URL> urls = new HashSet<URL>()
    files.each { f->
      // println "f: $f"
      urls.add(f.toURI().toURL())
      if (f.isDirectory()) {
        f.listFiles().each { l->
          if (l.isFile()) {
            urls.add(l.toURI().toURL())
            // println "l: $l"
          }
        }
      }
    }

    URL[] arr = new URL[urls.size()]
    arr = urls.toArray(arr)

    ClassLoader tccl = Thread.currentThread().getContextClassLoader()
    ParentLastURLClassLoader ucl = new ParentLastURLClassLoader(arr, tccl)
    Thread.currentThread().setContextClassLoader(ucl)

    System.setProperty("vertx.mods", project.file(project.extensions.vertx.modDir).path)

    Vertx vertx;
    if (project.extensions.vertx.port > -1) {
      vertx = Vertx.newVertx(project.extensions.vertx.port, project.extensions.vertx.hostname)
    }
    else {
      vertx = Vertx.newVertx()
    }

    def lng = ucl.findResource('langs.properties')
    def mcl = ucl.loadClass('org.vertx.java.deploy.impl.VerticleManager')
    def manager = mcl.newInstance(vertx, defaultRepo)
    // VerticleManager manager = new VerticleManager(vertx, defaultRepo)

    JsonObject config = new JsonObject()
    File currentModDir = project.file(project.extensions.vertx.modDir)

    DeploymentHandler doneHandler = new DeploymentHandler(1)

    try {
      manager.deployMod(moduleName, config, instances, currentModDir, doneHandler)
      doneHandler.await(moduleDownloadTimeout, TimeUnit.SECONDS);

      System.console().readLine("Type 'quit' to stop...")
    }
    catch (Throwable e) {
      // log
      e.printStackTrace()
    }
    finally {
      doneHandler.getDeployments().each { id->
        manager.undeploy(id, new VoidHandler(1))
      }
    }

  }

  public void setInstances(int instances) {
    this.instances = instances
  }

  public int getInstances() {
    this.instances
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName
  }

  public String getModuleName() {
    this.moduleName
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

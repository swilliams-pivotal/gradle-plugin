package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection


class AbstractVertxInstanceTask extends DefaultTask {

  def manager

  def container

  ClassLoader loader

  public void initVerticleManager(config) {

    FileCollection classpath = project.configurations.provided + project.configurations.vertxIntegRuntime
    def urlList = classpath.files.collect { File f->
      println f.absolutePath
      f.toURI().toURL()
    }

    String loggingProperties
    if (project.extensions.vertx.loggingProperties == null) { // ! should do this
      URL url = getClass().getClassLoader().getResource('logging.properties')
      urlList.add url
      loggingProperties = url.toURI().path
    }
    else {
      loggingProperties = project.extensions.vertx.loggingProperties
    }

    println "loggingProperties: " + loggingProperties

    URL[] urls = new URL[urlList.size()]
    urlList.toArray(urls)

    // System.setProperty('java.util.logging.config.file', loggingProperties)
    System.setProperty('vertx.mods', project.file(project.extensions.vertx.tempMods).absolutePath)

    println 'java.util.logging.config.file=' + loggingProperties
    println 'vertx.mods=' + System.getProperty('vertx.mods')

    ClassLoader parent = Thread.currentThread().getContextClassLoader()
    this.loader = new URLClassLoader(parent, urls)
    Thread.currentThread().setContextClassLoader(loader)

    Class vertxClass = Class.forName('org.vertx.java.core.impl.DefaultVertx', true, loader)
    Class managerClass = Class.forName('org.vertx.java.deploy.impl.VerticleManager', true, loader)
    Class containerClass = Class.forName('org.vertx.java.deploy.Container', true, loader)

//    def vertx = vertxClass.newInstance(project.extensions.vertx.port, project.extensions.vertx.host)
    def vertx = vertxClass.newInstance(project.extensions.vertx.port, project.extensions.vertx.host)
    this.manager = managerClass.newInstance(vertx)
    this.container = containerClass.newInstance(manager)

    addShutdownHook { manager.unblock() }
  }

  def waitForever() {
    manager?.block()
  }

  def toJSON(Map map) {
    Class jsonClass = Class.forName('org.vertx.java.core.json.JsonObject', true, loader)
    jsonClass.newInstance(map)
  }

}

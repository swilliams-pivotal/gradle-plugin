package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection
import org.gradle.util.MutableURLClassLoader
import org.vertx.java.core.Handler


class AbstractVertxInstanceTask extends DefaultTask {

  def static DEFAULT_VERTX = 'org.vertx.java.core.impl.DefaultVertx'
  def static GROOVY_VERTX = 'org.vertx.groovy.core.Vertx'
  def static VERTICLE_MANAGER = 'org.vertx.java.deploy.impl.VerticleManager'
  def static CONTAINER = 'org.vertx.java.deploy.Container'
  def static JSON_OBJECT = 'org.vertx.java.core.json.JsonObject'

  def manager

  def container

  private ClassLoader loader

  public void load(config) throws Exception {

    FileCollection classpath = project.configurations.provided + project.configurations.testCompile

    // add additional classpath as applicable
    if (config.classpath) classpath += config.classpath
    if (config.hasProperty('urls')) classpath + config.urls

    System.getProperties().putAll(config.systemProperties)

    // ClassLoading funkiness begins here...
    this.loader = loadClasspath(classpath)
    Thread.currentThread().setContextClassLoader(loader)

    // DEBUG!
    println "langs.properties: " + loader.findResource('langs.properties')
    println "logging.properties: " + loader.findResource('logging.properties')
    println "cluster.xml: " + loader.findResource('cluster.xml')
    println "vertx.mods: ${System.getProperty('vertx.mods')}"

    Class vertxClass = loader.loadClass(DEFAULT_VERTX)
    Class vertxGroovyClass = loader.loadClass(GROOVY_VERTX)
    Class managerClass = loader.loadClass(VERTICLE_MANAGER)
    Class containerClass = loader.loadClass(CONTAINER)

    def vertx = vertxClass.newInstance(project.extensions.vertx.port, project.extensions.vertx.host)
    def vertg = vertxGroovyClass.newInstance(vertx)
    this.manager = managerClass.newInstance(vertx)
    this.container = containerClass.newInstance(manager)

    addShutdownHook { manager?.unblock() }
  }

  private URLClassLoader loadClasspath(FileCollection classpath) {

    def urls = classpath.files.collect { File f-> 
      if (f.name.endsWith('.jar')) {
        new URL('jar:file:' + f.absolutePath + '!/')  // ugh.
      }
      else {
        f.toURI().toURL()
      }
    }

    URL[] arr = new URL[urls.size()]
    arr = urls.toArray(arr)

    new URLClassLoader(arr)
  }

  protected void waitForever() {
    manager?.block()
  }

  protected void cleanup() {
    loader?.close()
  }

  def mapToJSON(Map map) {
    loader?.loadClass(JSON_OBJECT).newInstance(map)
  }

}

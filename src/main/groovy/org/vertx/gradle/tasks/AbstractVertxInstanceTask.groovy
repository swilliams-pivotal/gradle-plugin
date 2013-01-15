package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection

class AbstractVertxInstanceTask extends DefaultTask {

  def manager

  def container

  public void initVerticleManager(config) {

    FileCollection classpath = project.configurations.provided + project.configurations.vertxIntegRuntime
    def urlList = classpath.files.collect { File f->  
      f.toURI().toURL()
    }

    URL[] urls = new URL[urlList.size()]
    urlList.toArray(urls)

    ClassLoader loader = new URLClassLoader(urls)
    Class vertxClass = Class.forName('org.vertx.java.core.impl.DefaultVertx', true, loader)
    Class managerClass = Class.forName('org.vertx.java.deploy.impl.VerticleManager', true, loader)
    Class containerClass = Class.forName('org.vertx.java.deploy.Container', true, loader)
    Class jsonClass = Class.forName('org.vertx.java.core.json.JsonObject', true, loader)

    def vertx = vertxClass.newInstance(project.extensions.vertx.port, 'localhost')
    def json = jsonClass.newInstance()
    this.manager = managerClass.newInstance(vertx)
    this.container = containerClass.newInstance(manager)

    addShutdownHook { manager.unblock() }

    System.setProperty('vertx.mods', project.file(project.extensions.vertxInteg.modsDir).absolutePath)

    println 'vertx.mods=' + System.getProperty('vertx.mods')

    container.deployModule(config.name, json, config.instances)
    manager.block()

  }

}

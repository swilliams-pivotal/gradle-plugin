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


class RunVertxModule extends DefaultTask {

  RunVertxModuleExtension config

  @TaskAction
  def runVertxModule() {

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

    def vertx = vertxClass.newInstance()
    def manager = managerClass.newInstance(vertx)
    def container = containerClass.newInstance(manager)
    def json = jsonClass.newInstance()

    container.deployModule(config.name, json, config.instances)
    manager.block()

  }

}

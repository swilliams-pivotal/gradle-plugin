package org.vertx.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction


class RunVerticle extends DefaultTask {

  RunVerticleExtension config

  @TaskAction
  def runVertx() {

    FileCollection classpath = project.configurations.provided + project.configurations.vertxInteg
    def urlList = classpath.files.collect { File f->  
      f.toURI().toURL()
    }

    URL[] urls = new URL[urlList.size()]
    urlList.toArray(urls)

    ClassLoader loader = new URLClassLoader(urls)
    Class vertxClass = Class.forName('org.vertx.java.core.impl.DefaultVertx', true, loader)
    Class managerClass = Class.forName('org.vertx.java.deploy.impl.VerticleManager', true, loader)

    def vertx = vertxClass.newInstance()
    def manager = managerClass.newInstance(vertx)

  }

}

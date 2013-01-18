/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vertx.gradle.tasks

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.util.ConfigureUtil


/**
 * @author pidster
 *
 */
class RunVertxModuleExtension {

  private final Project project

  String name

  int instances = 1

  Map json = [:]

  File modDir

  Properties systemProperties = new Properties()

  FileCollection classpath

  public RunVertxModuleExtension(Project project) {
    this.project = project
    this.name = "${project.modulename}-v${project.version}"
    this.modDir = project.file('build/mod')
    this.classpath = project.files('src/vertxInteg/resources')
  }

  public RunVertxModuleExtension vertxModule(Closure closure) {
    ConfigureUtil.configure(closure, this)
    this
  }

  public RunVertxModuleExtension name(String name) {
    this.name = name
    this
  }

  public RunVertxModuleExtension instances(int instances) {
    this.instances = instances
    this
  }

  public RunVertxModuleExtension json(Map json) {
    this.json = json
    this
  }

  public RunVertxModuleExtension modDir(File modDir) {
    this.modDir = modDir
    this
  }

  public RunVertxModuleExtension systemProperty(String name, String value) {
    systemProperties.setProperty(name, value)
    this
  }

}

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

import java.io.File;
import java.util.Map;
import java.util.Properties;

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.file.FileCollection;
import org.gradle.util.ConfigureUtil
import org.vertx.java.core.json.JsonObject;



/**
 * @author pidster
 *
 */
class RunVerticleExtension {

  private final Project project

  boolean worker

  String main

  String[] urls

  int instances = 1

  JsonObject json = new JsonObject()

  File modDir

  Properties systemProperties = new Properties()

  FileCollection classpath

  public RunVerticleExtension(Project project) {
    this.project = project
    this.modDir = project.file('build/mod')
    this.classpath = project.files('src/vertxInteg/resources')
  }

  public RunVerticleExtension verticle(Closure closure) {
    ConfigureUtil.configure(closure, this)
    this
  }

  public RunVerticleExtension main(String main) {
    this.main = main
    this
  }

  public RunVerticleExtension json(Map json) {
    this.json = json
    this
  }

  public RunVerticleExtension urls(String[] urls) {
    this.urls = urls
    this
  }

  public RunVerticleExtension instances(int instances) {
    this.instances = instances
    this
  }

  public RunVerticleExtension modDir(File modDir) {
    this.modDir = modDir
    this
  }

  public RunVerticleExtension systemProperty(String name, String value) {
    systemProperties.setProperty(name, value)
    this
  }

}

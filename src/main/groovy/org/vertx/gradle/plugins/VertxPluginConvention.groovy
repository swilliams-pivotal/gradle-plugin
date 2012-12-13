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
package org.vertx.gradle.plugins

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil



/**
 * @author pidster
 *
 */
class VertxPluginConvention {

  private final Project project

  private long timeout

  private long startPort

  private long stopPort

  private File modsDir

  public VertxPluginConvention(Project project) {
    this.project = project
  }

  public VertxPluginConvention vertx(Closure closure) {
    ConfigureUtil.configure(closure, this)
    this
  }

  public VertxPluginConvention timeout(long timeout) {
    this.timeout = timeout
    this
  }

  public VertxPluginConvention testTimeout(long timeout) {
    this.timeout = timeout
    this
  }

  public VertxPluginConvention startPort(long startPort) {
    this.startPort = startPort
    this
  }

  public VertxPluginConvention stopPort(long stopPort) {
    this.stopPort = stopPort
    this
  }

  public VertxPluginConvention modsDir(File modsDir) {
    this.modsDir = modsDir
    this
  }

  public VertxPluginConvention modsDir(String modsDir) {
    this.modsDir = project.file(modsDir)
    this
  }

}

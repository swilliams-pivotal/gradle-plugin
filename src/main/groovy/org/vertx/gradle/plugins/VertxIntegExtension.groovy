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

import groovy.lang.Closure;

import org.gradle.api.Project;
import org.gradle.util.ConfigureUtil


class VertxIntegExtension {

  boolean showStandardStreams = false
  long testTimeout = 15
  int port = -1
  String hostname = 'localhost'
  String modDir = 'build/mod'
  String modsDir = 'build/tmp/mods-test'

  private Project project

  def VertxIntegExtension(Project project) {
    this.project = project
  }

  public VertxIntegExtension vertxInteg(Closure closure) {
    ConfigureUtil.configure(closure, this)
    this
  }
  
  public VertxIntegExtension showStandardStreams(boolean showStandardStreams) {
    this.showStandardStreams = showStandardStreams
    this
  }

  public VertxIntegExtension testTimeout(long testTimeout) {
    this.testTimeout = testTimeout
    this
  }

  public VertxIntegExtension port(int port) {
    this.port = port
    this
  }

  public VertxIntegExtension hostname(String hostname) {
    this.hostname = hostname
    this
  }

  public VertxIntegExtension modDir(String modDir) {
    this.modDir = modDir
    this
  }

  public VertxIntegExtension modsDir(String modsDir) {
    this.modsDir = modsDir
    this
  }

}

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


class VertxPluginExtension {

  long testTimeout = 15
  int port = -1
  String hostname = 'localhost'
  String modDir = 'build/mod'
  String modsDir = 'build/tmp/mods-test'

//  public VertxPluginExtension(Project project) {
//    this.project = project
//  }

  public VertxPluginExtension vertx(Closure closure) {
    ConfigureUtil.configure(closure, this)
    this
  }

  public VertxPluginExtension testTimeout(long testTimeout) {
    println "setting testTimeout to $testTimeout"
    this.testTimeout = testTimeout
    this
  }

  public VertxPluginExtension port(int port) {
    println "setting port to $port"
    this.port = port
    this
  }

  public VertxPluginExtension hostname(String hostname) {
    println "setting hostname to $hostname"
    this.hostname = hostname
    this
  }

  public VertxPluginExtension modDir(String modDir) {
    println "setting modDir to $modDir"
    this.modDir = modDir
    this
  }

  public VertxPluginExtension modsDir(String modsDir) {
    println "setting modsDir to $modsDir"
    this.modsDir = modsDir
    this
  }


//  public long getTestTimeout() {
//    this.testTimeout;
//  }
//
//  public void setTestTimeout(long testTimeout) {
//    this.testTimeout = testTimeout;
//  }

}

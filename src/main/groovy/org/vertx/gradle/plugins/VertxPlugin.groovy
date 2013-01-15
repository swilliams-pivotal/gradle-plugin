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

import org.gradle.api.artifacts.Configuration
import org.gradle.api.Action
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.testing.Test

import org.vertx.gradle.tasks.RunVerticle
import org.vertx.gradle.tasks.RunVerticleExtension;
import org.vertx.gradle.tasks.RunVertxModule
import org.vertx.gradle.tasks.RunVertxModuleExtension;


class VertxPlugin implements Plugin<Project> {

  void apply(Project project) {
    project.apply plugin: 'java'

    RunVerticleExtension verticleConfig = project.extensions.create("verticle", RunVerticleExtension, project)
    RunVertxModuleExtension vertxModuleConfig = project.extensions.create("vertxModule", RunVertxModuleExtension, project)

    VertxInstanceExtension vertxConfig = project.extensions.create("vertx", VertxInstanceExtension, project)
    VertxIntegExtension integConfig = project.extensions.create("vertxInteg", VertxIntegExtension, project)

    project.with {
      configurations {
        provided {
          visible = false
        }
        compile.extendsFrom provided
        runtime.extendsFrom provided
      }

      sourceSets {
        vertxInteg {
          compileClasspath += test.output + test.compileClasspath
          runtimeClasspath += test.output + test.runtimeClasspath
        }
      }
    }

    project.task([type: Copy, dependsOn: 'classes'], 'prepareVertxModule', {
      group = ''
      description = ''
      into "build/mod/${project.modulename}-v${project.version}"
      from project.sourceSets.main.output.classesDir
      from project.sourceSets.main.output.resourcesDir
      into( 'lib' ) { 
        from (project.configurations.runtime - (project.configurations.provided + project.configurations.groovy))
      }
    })

    project.task([type: Sync, dependsOn: ['prepareVertxModule']], 'prepareVertxInteg', {
      group = ''
      description = ''
      from project.extensions.vertxInteg.modDir
      into project.extensions.vertxInteg.modsDir
    })

    def vertxPackageModule = project.task([type: Zip, dependsOn: ['prepareVertxModule']], 'vertxPackageModule', {
      group = 'vert.x'
      description = "Assembles a vert.x module in 'mod.zip' format"
      destinationDir = project.file('build/libs')
      archiveName = 'mod.zip'
      from project.file(project.extensions.vertxInteg.modDir)
    })

    // Ensure package is built 
    project.artifacts.add "archives", vertxPackageModule

    Test vertxInteg = project.task([type: Test, dependsOn: ['prepareVertxModule', 'prepareVertxInteg', 'vertxIntegClasses']], 'vertxInteg', {
      group = 'vert.x'
      description = 'Run vert.x integration tests'

      testClassesDir = project.sourceSets.vertxInteg.output.classesDir

      // Add stuff we DO need
      classpath += project.configurations.vertxIntegRuntime
      classpath += project.sourceSets.vertxInteg.output
      // Remove stuff we DO NOT need
      classpath -= project.sourceSets.main.output

      // FIXME lazily evaluate these:
      testLogging.showStandardStreams = true
    })

    project.afterEvaluate(new Action<Project>() {
      public void execute(Project p) {
        p.tasks.withType(Test, new Action<Test>() {
          public void execute(Test test) {
            vertxInteg.systemProperty 'vertx.test.timeout', project.extensions.vertxInteg.testTimeout
            vertxInteg.systemProperty 'vertx.mods', project.extensions.vertxInteg.modsDir
            vertxInteg.systemProperty 'vertx.version', "${project.vertxVersion}"
            vertxInteg.testLogging.showStandardStreams = project.extensions.vertxInteg.showStandardStreams
          }
        })
      }
    })

    project.tasks.findByName("check").dependsOn vertxInteg

    RunVerticle vertxRunVerticle = project.task("vertxRunVerticle", type: RunVerticle, dependsOn: ['prepareVertxModule', 'prepareVertxInteg', 'vertxIntegClasses'])
    vertxRunVerticle.config = verticleConfig

    RunVertxModule vertxRunModule = project.task("vertxRunModule", type: RunVertxModule, dependsOn: ['prepareVertxModule', 'prepareVertxInteg', 'vertxIntegClasses'])
    vertxRunModule.config = vertxModuleConfig
  }
}

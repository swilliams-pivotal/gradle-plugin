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
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.testing.Test

import org.vertx.gradle.tasks.RunVertxModule


class VertxPlugin implements Plugin<Project> {

  void apply(Project project) {
    project.apply plugin: 'java'

    project.convention.plugins.vertxTest = new VertxPluginConvention(project)
    project.extensions.create("vertx", VertxInstanceExtension)

    project.with {
      configurations {
        vertxProvided
        compile.extendsFrom vertxProvided
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
      into( 'lib' ) { from project.configurations.compile }
    })

    project.task([type: Sync, dependsOn: ['prepareVertxModule']], 'prepareVertxInteg', {
      group = ''
      description = ''
      from 'build/mod'
      into 'build/tmp/mod-test'
    })

    project.task([type: Zip, dependsOn: ['prepareVertxModule']], 'vertxPackageModule', {
      group = 'vert.x'
      description = "Assembles a vert.x module in 'mod.zip' format"
      destinationDir = project.file('build/libs')
      archiveName = 'mod.zip'
      from project.file("build/mod")
    })

    project.task([type: Test, dependsOn: ['prepareVertxModule', 'prepareVertxInteg', 'vertxIntegClasses']], 'vertxInteg', {
      group = 'vert.x'
      description = 'Run vert.x integration tests'

      systemProperty 'vertx.test.timeout', project.convention.plugins.vertxTest.timeout
      systemProperty 'vertx.mods', "${project.projectDir}/build/tmp/mod-test"
      systemProperty 'vertx.version', "${project.version}"
    
      testLogging.showStandardStreams = true

      testClassesDir = project.sourceSets.vertxInteg.output.classesDir

      // Add stuff we DO need
      classpath += project.configurations.vertxIntegCompile
      classpath += project.configurations.vertxIntegRuntime
      classpath += project.sourceSets.vertxInteg.output
      // Remove stuff we DO NOT need
      classpath -= project.sourceSets.main.output
    })

    project.task([type: RunVertxModule, dependsOn: ['vertxIntegClasses']], 'vertxRunModule', {
      group = 'vert.x'
      description = 'Run vert.x integration tests'

//      // Add stuff we DO need
//      classpath += project.configurations.vertxProvided + project.configurations.vertxIntegCompile
//      classpath += project.files(project.sourceSets.vertxInteg.output.classesDir, project.sourceSets.vertxInteg.output.resourcesDir)
//      // Remove stuff we DO NOT need
//      classpath -= project.files(project.sourceSets.main.output.classesDir, project.sourceSets.main.output.resourcesDir)
    })

  }

}

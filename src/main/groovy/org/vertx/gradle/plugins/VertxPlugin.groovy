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


class VertxPlugin implements Plugin<Project> {

  void apply(Project project) {
    project.apply plugin: 'java'

    project.convention.plugins.vertxTest = new VertxPluginConvention(project)
    project.extensions.add("vertxTest", VertxTestPluginExtension)

    project.with {
      sourceSets {
        vertxInteg {
          compileClasspath += main.output + test.output
          runtimeClasspath += main.output + test.output
        }
      }
      
      configurations {
        vertxProvided
        compile.extendsFrom vertxProvided
        vertxIntegCompile.extendsFrom testCompile
        vertxIntegRuntime.extendsFrom testRuntime
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

    project.task([type: Zip, dependsOn: ['prepareVertxModule']], 'packageVertxModule', {
      group = 'vert.x'
      description = "Assembles a vert.x module in 'mod.zip' format"
      destinationDir = project.file('build/libs')
      archiveName = 'mod.zip'
      from project.file("build/mod")
    })

    project.task([type: Sync, dependsOn: ['prepareVertxModule']], 'prepareTestVertxModule', {
      group = ''
      description = ''
      from 'build/mod'
      into 'build/tmp/mod-test'
    })

    project.task([type: Test, dependsOn: ['prepareVertxModule', 'prepareTestVertxModule', 'vertxIntegClasses']], 'testVertxModule', {
      group = 'vert.x'
      description = 'Run vert.x integration tests'

      systemProperty 'vertx.test.timeout', 15
      systemProperty 'vertx.mods', "${project.projectDir}/build/tmp/mod-test"
      systemProperty 'vertx.version', "${project.version}"
    
      testLogging.showStandardStreams = true

      testClassesDir = project.sourceSets.vertxInteg.output.classesDir

      // Add stuff we DO need
      classpath += project.configurations.vertxProvided + project.configurations.vertxIntegCompile
      classpath += project.files(project.sourceSets.vertxInteg.output.classesDir, project.sourceSets.vertxInteg.output.resourcesDir)
      // Remove stuff we DO NOT need
      classpath -= project.files(project.sourceSets.main.output.classesDir, project.sourceSets.main.output.resourcesDir)
    })

  }

}

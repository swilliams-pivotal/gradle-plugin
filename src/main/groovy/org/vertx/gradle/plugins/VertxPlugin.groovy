package org.vertx.gradle.plugins

import org.gradle.api.artifacts.Configuration
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin

class VertxPlugin implements Plugin<Project> {

  void apply(Project project) {
    JavaPlugin javaPlugin = project.plugins.apply(JavaPlugin)

    project.with {
      configurations {
        vertxProvided
        vertxIntegCompile
      }

      sourceSets {
        main {
          compileClasspath = compileClasspath + configurations.vertxProvided
        }
        vertxInteg {
          compileClasspath = configurations.vertxProvided + configurations.testCompile
        }
      }

      task prepareVertxModule( type:Copy, dependsOn: 'classes' ) {
        into "build/mod/$modulename-v$version"
        from 'build/classes/main'
        from 'build/resources/main'
        into( 'lib' ) {
          from configurations.compile
        }
      }

      // Package into build/libs/mod.zip
      task vertxVertxPackage(type: Zip, dependsOn: 'prepareVertxModule' ) {
        group = 'vert.x'
        description = "Assembles a vert.x module in 'mod.zip' format"
        destinationDir = project.file('build/libs')
        archiveName = 'mod.zip'
        from project.file("build/mod")
      }

      task prepareVertxTest(type: Sync, dependsOn: 'prepareVertxModule' ) {
        from 'build/mod'
        into 'build/tmp/mod-test'
      }

      task vertxTest(type: Test, dependsOn: [ 'copyMod', 'prepareVertxTest', 'vertxIntegClasses' ]) {
        group = 'vert.x'
        description = 'Run vert.x integration tests'
      
        systemProperty 'vertx.test.timeout', 15
        systemProperty 'vertx.mods', "$projectDir/build/tmp/mod-test"
        systemProperty 'vertx.version', "$project.version"
      
        testLogging.showStandardStreams = true
        testClassesDir = sourceSets.vertxInteg.output.classesDir
      
        // Add stuff we need
        classpath += configurations.vertxProvided + configurations.testCompile
        classpath += files(sourceSets.vertxInteg.output.classesDir, sourceSets.vertxInteg.output.resourcesDir)
        // remove stuff we don't want
        classpath -= files(sourceSets.main.output.classesDir, sourceSets.main.output.resourcesDir)
      }
    }

  }

}

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

package org.vertx.gradle.tools.plugins

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTreeElement
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.bundling.Zip

//import org.vertx.gradle.tools.tasks.VertxTest
import org.vertx.gradle.tools.tasks.VertxTestConvention


/**
 * @author pidster
 *
 */
class VertxPlugin implements Plugin<Project> {

  public static final String VERTX_PROVIDED_CONFIGURATION = 'vertxProvided'

  public static final String VERTX_TEST_TASK = 'vertxTest'

  public static final String VERTX_TASK_GROUP = 'vert.x'


  void apply(Project project) {
    JavaPlugin javaPlugin = project.plugins.apply(JavaPlugin)

    Configuration vertxProvided =
        project.configurations
        .add(VERTX_PROVIDED_CONFIGURATION)
        .setVisible(false)
        .setTransitive(false)
        .setDescription("The $VERTX_PROVIDED_CONFIGURATION configuration libraries to be used for this vert.x project.")

    Configuration compile = project.configurations.findByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
    Configuration runtime = project.configurations.findByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME)
    Configuration testCompile = project.configurations.findByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME)
    Configuration testRuntime = project.configurations.findByName(JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME)
    // compile.extendsFrom vertxProvided

    // project.convention.plugins.vertx = new VertxPluginConvention(project)
    project.convention.plugins.vertxTest = new VertxTestConvention(project)

    JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)

    convention.sourceSets.main.compileClasspath = convention.sourceSets.main.compileClasspath.plus vertxProvided
    convention.sourceSets.main.runtimeClasspath = convention.sourceSets.main.runtimeClasspath.minus vertxProvided

    convention.sourceSets.test.compileClasspath = convention.sourceSets.test.compileClasspath.plus vertxProvided
    
    /*
    Test test = project.tasks.findByName('test')
    test.classpath = test.classpath.minus convention.sourceSets.main.runtimeClasspath

    Task build = project.tasks.getByName('build') // FIXME use static reference to build task
    Task assemble = project.tasks.getByName('assemble') // FIXME use static reference to assemble task

    Task prepareModule = project.tasks.add('prepareVertxModule', Copy)
    prepareModule.description = "Assembles a vert.x module in the build/mod/${project.name.replaceFirst('mod-', '')} dir"
    prepareModule.dependsOn assemble
    prepareModule.destinationDir = project.file("build/mod/${project.modulename}-v${project.version}")
    prepareModule.with {
      from(project.configurations.runtime) { into 'lib' }
      from 'build/classes/main'
      from 'build/resources/main'
      from 'src/main/conf'
    }

    Task packageModule = project.tasks.add('packageVertxModule', Zip)
    packageModule.group = VERTX_TASK_GROUP
    packageModule.description = "Assembles a vert.x module in 'mod.zip' format"
    packageModule.dependsOn prepareModule
    packageModule.destinationDir = project.file('build/libs')
    packageModule.archiveName = 'mod.zip'
    packageModule.from project.file("build/mod")
    */

    /*
     * TODO Add a Sync task that copies the output of the prepareModule
     * to the test-mods dir. 
     * 
     * test.dependsOn vertTest
     */
    /*
    Task prepareVertxTest = project.tasks.add('prepareVertxTest', Sync)
    prepareVertxTest.dependsOn prepareModule
    prepareVertxTest.from project.file('build/mod')  // TODO take from convention?
    prepareVertxTest.into project.file('build/tmp/test-mods') // TODO take from system property
    test.dependsOn prepareVertxTest
    */

    /*
     * TODO determine whether a custom vertx test task is a good idea
     * 
    Task vertxTest = project.tasks.add('vertxTest', Test)
    test.dependsOn vertxTest
     */
  }

}

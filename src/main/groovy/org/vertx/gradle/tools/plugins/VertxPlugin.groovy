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
import org.gradle.api.file.FileTreeElement
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.testing.Test

import org.vertx.gradle.tools.tasks.VertxTest
import org.vertx.gradle.tools.tasks.VertxTestConvention


/**
 * @author pidster
 *
 */
class VertxPlugin implements Plugin<Project> {
  
    public static final String VERTX_PROVIDED_CONFIGURATION = 'vertxProvided'
  
    public static final String VERTX_TEST_TASK = 'vertxTest'
  
	void apply(Project project) {
		JavaPlugin javaPlugin = project.plugins.apply(JavaPlugin)

        Configuration vertxProvided =
            project.configurations
                .add(VERTX_PROVIDED_CONFIGURATION)
                .setVisible(true)
                .setTransitive(false)
                .setDescription("The $VERTX_PROVIDED_CONFIGURATION runtime libraries to be used for this vert.x project.")

        Configuration compile = project.configurations
            .findByName(VertxToolsPlugin.COMPILE_CONFIGURATION)
        vertxProvided.extendsFrom compile
        
        JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
        convention.sourceSets.main.compileClasspath = vertxProvided
        
        project.convention.plugins.vertx = new VertxTestConvention(project)
        
        VertxTest vertxTest = project.tasks.add(VERTX_TEST_TASK, VertxTest)

        Task check = project.tasks.getByName('check')
        check.dependsOn vertxTest

        Task build = project.tasks.getByName('build')
        build.dependsOn vertxTest
	}

}

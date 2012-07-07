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

package org.vertx.gradle.tools.tasks

import org.gradle.api.internal.ConventionTask
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test


/**
 * @author pidster
 *
 */
class VertxTest extends ConventionTask {
  
    private long timeout = 30
  
    private File modsDir = project.file('build/tmp/mods-dir')

    @TaskAction
    public void testVertx() {
        JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
        
        Test test = project.tasks.add('vertxTestRunner', Test)
        test.systemProperty 'vertx.test.timeout', timeout
        test.systemProperty 'vertx.mods', modsDir.path
        test.classpath = test.classpath.minus(project.convention.sourceSets.main.runtimeClasspath)
    }
  
}

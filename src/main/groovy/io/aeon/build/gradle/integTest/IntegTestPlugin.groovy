package io.aeon.build.gradle.integTest

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.testing.Test

/**
 * @author pidster
 */
class IntegTestPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        IntegTestPluginExtension extension = project.extensions.create('integTest', IntegTestPluginExtension, project)

        // if 'java' plugin...
        if (project.plugins.hasPlugin('java')) {
            Configuration configuration = project.configurations.create('integTest')
            configuration.extendsFrom project.configurations.runtime

            JavaCompile javaCompile = project.tasks.create('integTestJavaCompile', JavaCompile, {
                dependsOn project.tasks.assemble
            })

            Test javaTest = project.tasks.create('integTestJava', Test, {
                dependsOn javaCompile
            })
            project.tasks.check.dependsOn javaTest
        }

        // if 'groovy' plugin
        if (project.plugins.hasPlugin('groovy')) {
            GroovyCompile groovyCompile = project.tasks.create('integTestGroovyCompile', GroovyCompile, {
                dependsOn project.tasks.assemble
            })

            Test groovyTest = project.tasks.create('integTestGroovy', Test, {
                dependsOn groovyCompile
            })
            project.tasks.check.dependsOn groovyTest
        }

        project.afterEvaluate {
            extension.onAfterEvaluate()
        }
    }

}

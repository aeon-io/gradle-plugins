package io.aeon.build.gradle.optional

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author pidster
 */
class OptionalScopePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        OptionalScopePluginExtension extension = project.extensions.create('optional', OptionalScopePluginExtension, project)

        if (project.plugins.hasPlugin('java')) {
            //
        }

        project.afterEvaluate {
            extension.onAfterEvaluate()
        }
    }

}

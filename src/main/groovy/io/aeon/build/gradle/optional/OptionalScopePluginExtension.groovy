package io.aeon.build.gradle.optional

import org.gradle.api.Project

/**
 * @author pidster
 */
class OptionalScopePluginExtension {

    private final Project project

    def OptionalScopePluginExtension(Project project) {
        this.project = project
    }

    void onAfterEvaluate() {

    }
}

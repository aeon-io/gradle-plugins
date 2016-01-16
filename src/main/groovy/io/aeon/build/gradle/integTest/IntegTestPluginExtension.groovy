package io.aeon.build.gradle.integTest

import org.gradle.api.Project

/**
 * @author pidster
 */
class IntegTestPluginExtension {

    private final Project project

    def IntegTestPluginExtension(Project project) {
        this.project = project
    }

    void onAfterEvaluate() {
        project.logger.debug('onAfterEvaluate()')
    }

}

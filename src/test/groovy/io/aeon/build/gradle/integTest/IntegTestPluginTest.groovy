package io.aeon.build.gradle.integTest

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author pidster
 */
class IntegTestPluginTest {

    @Test
    public void test() {
        Project project = ProjectBuilder.builder()
                .withName('integTestBuild')
                .build()

        project.pluginManager.apply 'java'
        project.pluginManager.apply 'groovy'
        project.pluginManager.apply 'io.aeon.integ-test'

        assertTrue(project.extensions.integTest instanceof IntegTestPluginExtension)

    }
}

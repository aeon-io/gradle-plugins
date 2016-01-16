package io.aeon.build.gradle.optional

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author pidster
 */
class OptionalScopePluginTest {

    @Test
    public void test() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'io.aeon.optional-scope'

        assertTrue(project.extensions.optional instanceof OptionalScopePluginExtension)

    }

}

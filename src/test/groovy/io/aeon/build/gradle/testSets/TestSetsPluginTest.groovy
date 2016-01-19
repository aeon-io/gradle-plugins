package io.aeon.build.gradle.testSets

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertTrue

/**
 * @author pidster
 */
class TestSetsPluginTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    public void test() {

        def buildFile = temporaryFolder.newFile('build.gradle')

        buildFile << """
            testSets {
                addSet 'integTest'
                addSet 'systemTest'
                integTest {
                    name 'integration'
                }
                systemTest {
                    name 'system'
                }
            }
        """

        Project project = ProjectBuilder.builder()
                .withProjectDir(temporaryFolder.root)
                .build()

        project.pluginManager.apply 'java'
        project.pluginManager.apply 'groovy'
        project.pluginManager.apply 'io.aeon.test-sets'

        assertTrue(project.extensions.testSets instanceof TestSetsPluginExtension)

//        List<File> pluginClasspath = []
//
//        BuildResult result = GradleRunner.create()
//                // .withPluginClasspath(pluginClasspath)
//                .withProjectDir(temporaryFolder.root)
//                .withDebug(true)
//                .withArguments("check")
//                .build()
    }
}

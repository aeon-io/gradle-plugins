package io.aeon.build.gradle.docker

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * @author pidster
 */
class DockerBuildPluginTest {

    @Test
    public void testPlugin() {
        Project project = ProjectBuilder.builder()
                .withName("testName")
                .build()

        project.setProperty("group", "testGroup")

        project.pluginManager.apply 'io.aeon.docker-build'

        assertTrue(project.extensions.dockerBuild instanceof DockerBuildPluginExtension)

        DockerBuildPluginExtension extension = project.extensions.dockerBuild
        assertEquals(extension.tag, 'testGroup/testName:latest')
        extension.dockerFile = 'Dockerfile'

        assertTrue(project.tasks.dockerClean instanceof Delete)
        assertTrue(project.tasks.dockerPrepare instanceof Copy)
        assertTrue(project.tasks.dockerImage instanceof Exec)
        assertTrue(project.tasks.dockerPush instanceof Exec)
    }

    @Test
    public void testPluginDsl() {
        Project project = ProjectBuilder.builder()
                .withName("testName")
                .build()

//        project.afterEvaluate {
//            testPluginDsl()
//        }
//
//        project.setProperty("group", "testGroup")
//
//        project.pluginManager.apply 'io.aeon.docker-build'
//
//        assertTrue(project.extensions.dockerBuild instanceof DockerBuildPluginExtension)
//
//        DockerBuildPluginExtension extension = project.extensions.dockerBuild
//        assertEquals(extension.tag, "testGroup/testName:latest")
//        extension.onAfterEvaluate()
//
//        assertTrue(project.tasks.dockerClean instanceof Delete)
//        assertTrue(project.tasks.dockerPrepare instanceof Copy)
//        assertTrue(project.tasks.dockerImage instanceof Exec)
//        assertTrue(project.tasks.dockerPush instanceof Exec)
    }

}

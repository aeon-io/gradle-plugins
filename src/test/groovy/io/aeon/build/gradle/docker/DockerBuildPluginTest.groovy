package io.aeon.build.gradle.docker

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

/**
 * @author pidster
 */
class DockerBuildPluginTest {

    @Test
    public void testPlugin() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'io.aeon.docker-build'

        Assert.assertTrue(project.extensions.dockerBuild instanceof DockerBuildPluginExtension)
        Assert.assertTrue(project.tasks.dockerClean instanceof Delete)
        Assert.assertTrue(project.tasks.dockerPrepare instanceof Copy)
        Assert.assertTrue(project.tasks.dockerImage instanceof Exec)
        Assert.assertTrue(project.tasks.dockerPush instanceof Exec)
    }

}

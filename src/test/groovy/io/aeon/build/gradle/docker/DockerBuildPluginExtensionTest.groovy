package io.aeon.build.gradle.docker

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 * @author pidster
 */
class DockerBuildPluginExtensionTest {

    @Test
    public void testExtension() {


        Project project = ProjectBuilder.builder()
                .build()

        DockerBuildPluginExtension extension = new DockerBuildPluginExtension(project)

        assertFalse(extension.quiet)
        assertFalse(extension.forceRemove)
        assertFalse(extension.noCache)
        assertFalse(extension.pull)

        assertTrue(extension.disableContentTrust)
        assertTrue(extension.remove)

        assertEquals(0, extension.cpuPeriod)
        assertEquals(0, extension.cpuQuota)
        assertEquals(0, extension.cpuShares)

        assertEquals(0, extension.dependencies.size())
        assertEquals(0, extension.resolvedFiles.size())

        assertEquals('', extension.cgroupParent)
        assertEquals('', extension.cpuSetCpus)
        assertEquals('', extension.cpuSetMems)
        assertEquals('', extension.memory)
        assertEquals('', extension.memorySwap)

        assertEquals('src/main/docker/Dockerfile', extension.dockerFile)

    }

}

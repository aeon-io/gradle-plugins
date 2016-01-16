package io.aeon.build.gradle.docker

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Ignore

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.gradle.testkit.runner.GradleRunner

import spock.lang.Specification

/**
 * // Gradle TestKit
 *
 * @author pidster
 */
@Ignore
class DockerBuildPluginDslSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir
    File buildFile
    File dockerFile

    def setup() {
        projectDir = temporaryFolder.root
        buildFile = temporaryFolder.newFile('build.gradle')
        dockerFile = temporaryFolder.newFile('Dockerfile')

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run 'functionalTestClasses' build task.")
        }

        def pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")

        dockerFile << """
        FROM aeonproject/java-jre
        MAINTAINER 'Aeon Builds <builds+test@pidster.com>'
        ENTRYPOINT [ 'java', '-version' ]
"""

        // Add the logic under test to the test build
        buildFile << """
            buildscript {
                dependencies {
                    classpath files($pluginClasspath)
                }
            }
    """
    }

    def 'test plugin' () {

        given:
        buildFile << """
            apply plugin: io.aeon.build.gradle.docker.DockerBuildPlugin

            dockerBuild {
                dockerFile 'Dockerfile'
                tag 'io.aeon/test1'
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments('dockerImage')
            .build()

        then:
        result.task(":dockerImage").outcome == SUCCESS
        // projectDir.file("Dockerfile").exists()
        true

    }

}

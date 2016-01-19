package io.aeon.build.gradle.docker

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec

/**
 * <p>
 *     The Docker Build Plugin adds an extension ({@code dockerBuild}) and
 *     a series of tasks to the project.
 *
 * <p><code><pre>
 *     plugins {
 *         id 'io.aeon.docker-build' version '0.1'
 *     }
 * </pre></code>
 * <p>
 *     Gradle Tasks added:
 * <ul>
 *     <li>{@code dockerClean}</li>
 *     <li>{@code dockerPrepare} (depends on dockerClean)</li>
 *     <li>{@code dockerImage} (depends on dockerPrepare)</li>
 *     <li>{@code dockerPush} (depends on dockerImage)</li>
 * </ul>
 *
 * @author pidster
 * @since 1.0
 *
 */
class DockerBuildPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (!System.getenv().containsKey("DOCKER_HOST")) {
            println "DOCKER_HOST not detected, DockerBuildPlugin may not work correctly!!!"
        }

        DockerBuildPluginExtension extension = project.extensions.create("dockerBuild", DockerBuildPluginExtension, project)

        Delete clean = project.tasks.create('dockerClean', Delete, {
            group = 'Docker'
            description = "Clean the Docker build directory."
        })

        Copy prepare = project.tasks.create('dockerPrepare', Copy, {
            group = 'Docker'
            description = "Prepare the Docker build directory."
            dependsOn clean
        })

        // TODO variable parsing step in dockerFile

        Exec image = project.tasks.create('dockerImage', Exec, {
            group = 'Docker'
            description = "Build the Docker image."
            dependsOn prepare
        })

        Exec push = project.tasks.create('dockerPush', Exec, {
            group = 'Docker'
            description = "Push Docker image to configured registry."
            dependsOn image
        })

        project.afterEvaluate {
            extension.onAfterEvaluate()

            String dockerDir = "${project.buildDir}/docker"

            clean.delete dockerDir

            prepare.with {
                from(extension.resolvedDockerfile) {
                    rename { fileName ->
                        fileName.replace(extension.resolvedDockerfile.getName(), 'Dockerfile')
                    }
                }
                from extension.dependencies*.outputs
                from extension.resolvedFiles
                into dockerDir
            }

            List<String> commandLineArgs = ['docker', 'build', '--quiet=' + extension.quiet, '-t', extension.tag, '.']

            extension.buildArgs.each { k, v ->
                commandLineArgs.add(2, "--build-arg=$k=$v")
            }

            image.with {
                workingDir dockerDir
                commandLine commandLineArgs
                dependsOn extension.getDependencies()
            }

            push.with {
                workingDir dockerDir
                commandLine 'docker', 'push', extension.tag
            }
        }
    }

}
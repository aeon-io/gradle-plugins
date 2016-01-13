package io.aeon.build.gradle.docker

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec

/**
 *
 *
 *
 * @author pidster
 *
 */
class DockerBuildPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (!System.getenv().containsKey("DOCKER_HOST")) {
            println "DOCKER_HOST not detected, DockerBuildPlugin may not work correctly!!!"
        }

        DockerBuildPluginExtension extension = project.extensions.create("dockerBuild", DockerBuildPluginExtension, project)

        if (!project.configurations.findByName('docker')) {
            project.configurations.create('docker')
        }

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

            List<String> buildArgsParams = []
            extension.buildArgs.each { k, v ->
                buildArgsParams.add("--build-arg=$k=$v")
            }

            List<String> commandLineArgs = ['docker', 'build', '-t', extension.name, '-q', extension.quiet, '.']
            commandLineArgs.addAll(2, buildArgsParams)

            image.with {
                workingDir dockerDir
                commandLine commandLineArgs
                dependsOn extension.getDependencies()
            }

            push.with {
                workingDir dockerDir
                commandLine 'docker', 'push', extension.name
            }
        }
    }

}
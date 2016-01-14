/**
 * <p>
 *     A Gradle Plugin for building Docker containers. Executes a locally
 *     installed {@code docker build} command, which is therefore a
 *     pre-requisite.
 *
 * <p>
 *     Apply the plugin using the Gradle 2.1+ {@code plugins} DSL element.
 *
 * <p><code><pre>
 *     plugins {
 *         id 'io.aeon.docker-build' version '0.1'
 *     }
 *     ...
 *     dockerBuild {
 *         name "organisation/project:$tagVersion"
 *         dockerFile 'src/main/docker/Dockerfile'
 *         dependsOn myGradleTask
 *         buildArg 'version', project.version
 *     }
 * </pre></code>
 *
 *
 * @author pidster
 * @since 1.0
 */
package io.aeon.build.gradle.docker
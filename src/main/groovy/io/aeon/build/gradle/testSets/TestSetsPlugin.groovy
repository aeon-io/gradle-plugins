package io.aeon.build.gradle.testSets

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test

/**
 * @author pidster
 */
class TestSetsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        TestSetsPluginExtension extension = project.extensions.create('testSets', TestSetsPluginExtension, project)

        project.afterEvaluate {
            extension.onAfterEvaluate()

            SourceSetContainer sourceSets = project.sourceSets
            ConfigurationContainer configurations = project.configurations

            extension.testSetMap.each { String name, Closure<?> closure ->
                SourceSet sourceSet = sourceSets.create(name, {
                    resources.srcDir project.file("src/${name}/resources")
                    compileClasspath += sourceSets.main.output + configurations.testRuntime
                    runtimeClasspath += output + compileClasspath
                })

                ['java', 'groovy'].each { String lang ->
                    if (project.pluginManager.hasPlugin(lang)) {
                        sourceSet."$lang".srcDir project.file("src/${name}/${lang}")
                    }
                }

                String taskName = "${name}Test".replaceAll('TestTest', 'Test')

                def map = [
                        (Task.TASK_NAME): taskName,
                        (Task.TASK_TYPE): Test,
                        (Task.TASK_GROUP): 'verification',
                        (Task.TASK_DESCRIPTION): "Run the ${name} tests",
                        (Task.TASK_DEPENDS_ON): [project.tasks."${name}Classes"]
                ]

                Test testTask = project.tasks.create(map, closure) as Test
                testTask.testSrcDirs = sourceSet.allSource.srcDirs as List<File>
                testTask.testClassesDir = project.file("${project.buildDir}/classes/${name}")
                testTask.classpath = sourceSet.runtimeClasspath
                testTask.shouldRunAfter project.tasks.test
                project.tasks.check.dependsOn testTask

//                testTask.dependsOn.remove(project.tasks.testClasses)
//                testTask.ignoreFailures = false

//                project.tasks.scopedTestClasses.doLast {
//                    println "classes >>> outputs.files.files: " + outputs.files.files
//                    println "classes >>> scopedTest.files   : " + project.file("${project.buildDir}/classes/${name}").listFiles().each { File f -> f.listFiles() }.flatten()
//
//                    println "scopedTest.candidateClassFiles: " + testTask.candidateClassFiles
//                    println "scopedTest.classpath          : " + testTask.classpath.files
//                    println "scopedTest.testClassesDir     : " + testTask.testClassesDir
//                    println "scopedTest.testSrcDirs        : " + testTask.testSrcDirs
//                    println "scopedTest.inputs             : " + testTask.inputs.files.files
//                    println "scopedTest.outputs            : " + testTask.outputs.files.files
//
//                    testTask.properties.each { k,v ->
//                        println "scopedTest.properties.$k : " + v
//                    }
//                }

//                project.tasks.compileScopedTestJava.doLast {
//                    println ">>>>>>>>>>> outputs.files.files: " + outputs.files.files
//                    println ">>> sourceSet.output.classesDir: " + sourceSet.output.classesDir
//                    println ""
//                }

            }
        }
    }

}

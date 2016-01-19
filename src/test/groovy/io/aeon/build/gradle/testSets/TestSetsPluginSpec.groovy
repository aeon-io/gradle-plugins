package io.aeon.build.gradle.testSets

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * @author pidster
 */
class TestSetsPluginSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    File buildFile

    def setup() {

        buildFile = temporaryFolder.newFile('build.gradle')
        File mainSourceDir = temporaryFolder.newFolder('src', 'main', 'java', 'tester')
        File mainFile = new File(mainSourceDir, 'Simple.java')
        mainFile.write("""
        package tester;

        public class Simple {
            public void foo() {
                System.out.println("Hello World");
            }
        }
        """)

        File testSourceDir = temporaryFolder.newFolder('src', 'scopedTest', 'java', 'tester')
        File testFile = new File(testSourceDir, 'SimpleTest.java')
        testFile.write("""
        package tester;

        import org.junit.Test;

        public class SimpleTest {

            @Test
            public void test() {
                // test fixture
                System.out.println("TEST RUNNING");
                new Simple().foo();
            }
        }
        """)

//        File testClassesDir = temporaryFolder.newFolder('build', 'classes', 'test')

        File resDir = temporaryFolder.newFolder('src', 'scopedTest', 'resources')
        File testRes = new File(resDir, 'test.properties')
        testRes.write("""
        foo.bar=123
        """)

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run 'createClasspathManifest' build task.")
        }

        def pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(', ')

        // Add the logic under test to the test build
        buildFile << """
            buildscript {
                dependencies {
                    classpath files($pluginClasspath)
                }
            }

            apply plugin: 'java'
            apply plugin: io.aeon.build.gradle.testSets.TestSetsPlugin

            repositories {
                mavenCentral()
            }

            dependencies {
                testCompile "junit:junit:4.12"
            }
        """
    }

    def 'assemble scoped test and check task graph' () {

        given:
        buildFile << """
            apply plugin: 'groovy'

            testSets {
                define 'scopedTest'
                anotherScopedTest {
                    testLogging {
                        showStandardStreams = true
                    }
                }
            }
        """

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withDebug(true)
                .withArguments("tasks", "--all", "--stacktrace")
                .build()

        then:
        result.task(":tasks").outcome == SUCCESS
        // result.output.contains('anotherScopedTestClasses - Assembles another scoped test classes. [classes]')
        result.output.contains('scopedTestClasses - Assembles scoped test classes. [classes]')
        result.output.contains('compileScopedTestGroovy - Compiles the scopedTest Groovy source.')
        result.output.contains('compileScopedTestJava - Compiles scoped test Java source.')
        result.output.contains('processScopedTestResources - Processes scoped test resources.')
        result.output.contains('check - Runs all checks. [anotherScopedTest, scopedTest, test]')
        result.output.contains('scopedTest - Run the scopedTest tests [classes, scopedTestClasses]')
    }

    def 'execute sample test in new scope' () {

        given:
        buildFile << """

            testSets {
                scopedTest {
                    testLogging {
                        events "passed", "skipped", "failed", "standardOut", "standardError"
                        showStandardStreams = true
                    }
                }
            }

            test {
                testLogging {
                    events "passed", "skipped", "failed", "standardOut", "standardError"
                    showStandardStreams = true
                }
            }
        """

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(temporaryFolder.root)
                .withDebug(true)
                .withGradleVersion("2.9")
                .withArguments("assemble", "scopedTest", "--debug", "--stacktrace")
                .build()

        println result.output

        then:
        result.task(":scopedTest").outcome == SUCCESS

    }

}

package io.aeon.build.gradle.testSets

import static org.junit.Assert.assertEquals

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 * @author pidster
 */
public class TestSetsPluginExtensionTests {

    @Test
    public void test() {

        Project project = ProjectBuilder.builder()
                .build()

        TestSetsPluginExtension extension = new TestSetsPluginExtension(project)

        extension.define('foo', 'bar')
        assertEquals(2, extension.getTestSetMap().size())

        extension.scopedTest {}
        assertEquals(3, extension.testSetMap.size())
    }

}

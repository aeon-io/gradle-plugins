package io.aeon.build.gradle.testSets

import org.gradle.api.Project

/**
 * @author pidster
 */
class TestSetsPluginExtension {

    private final Project project

    private final Map<String, Closure> testSetMap = new HashMap<>()

    def TestSetsPluginExtension(Project project) {
        this.project = project
    }

    public void define(String... names) {
        for (String name : names) {
            testSetMap.put(name, {})
        }
    }

    def methodMissing(String name, def args) {
        if (args != null && args instanceof Closure) {
            testSetMap.put(name, (Closure<?>) args)
        }
        else {
            testSetMap.put(name, {})
        }
    }

    Map<String, Closure> getTestSetMap() {
        return testSetMap
    }

}

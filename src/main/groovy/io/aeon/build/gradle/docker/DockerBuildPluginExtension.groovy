package io.aeon.build.gradle.docker

import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author pidster
 */
class DockerBuildPluginExtension {

    private final Project project

    private boolean quiet = false

    private String name = "${project.group}/${project.name}"
    private String dockerFile = 'Dockerfile'

    private Set<Task> dependencies = Collections.emptySet()
    private Set<String> files = Collections.emptySet()

    private File resolvedDockerfile = null
    private Set<File> resolvedFiles = Collections.emptySet()
    private Map<String, String> buildArgs = new HashMap<>()

    public DockerBuildPluginExtension(Project project) {
        this.project = project
    }

    public boolean isQuiet() {
        return quiet
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet
    }

    public void buildArg(String name, String value) {
        buildArgs.put(name.trim(), value.trim())
    }

    public void setName(String name) {
        this.name = name
    }

    public String getName() {
        return name
    }

    public void setDockerFile(String dockerFile) {
        this.dockerFile = dockerFile
    }

    public void dependsOn(Task... args) {
        Set<Task> set = new HashSet<>(Arrays.asList(args));
        this.dependencies = Collections.unmodifiableSet(set);
    }

    public Set<Task> getDependencies() {
        return dependencies
    }

    public void files(String... args) {
        Set<String> set = new HashSet<>(Arrays.asList(args));
        this.files = Collections.unmodifiableSet(set);
    }

    public File getResolvedDockerfile() {
        return resolvedDockerfile
    }

    public Set<String> getResolvedFiles() {
        return resolvedFiles
    }

    public Map<String, String> getBuildArgs() {
        return Collections.unmodifiableMap(buildArgs)
    }

    public void onAfterEvaluate() {
        Objects.requireNonNull(name, "'name' is required")

        this.resolvedDockerfile = project.file(dockerFile)
        if (!resolvedDockerfile.exists()) {
            throw new IllegalStateException("$dockerFile does not exist")
        }

        Set<File> set = new HashSet<>();
        for (String file : files) {
            def resolvedFile = project.file(file)
            if (!resolvedFile.exists()) {
                throw new IllegalStateException("$resolvedFile does not exist")
            }

            set.add(resolvedFile)
        }

        this.resolvedFiles = Collections.unmodifiableSet(set)
    }
}

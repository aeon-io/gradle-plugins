package io.aeon.build.gradle.docker

import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Gradle Extension configuring the Docker Build command.
 * <p>
 * <code><pre>
 *     dockerBuild {
 *         name "${project.group}/${project.name}:${project.dockerTagVersion}"
 *         dockerFile 'src/main/docker/Dockerfile'
 *         dependsOn someTask, myTask, anotherTask
 *         files 'src/main/config/logback.xml', 'src/main/config/project.properties'
 *         buildArg 'version', project.version
 *     }
 * </pre></code>
 *
 * @author pidster
 * @since 1.0
 */
class DockerBuildPluginExtension {

    private final Project project

    private final String tagOrg = project.properties?.group ?: System.env.USER_NAME

    private final String tagVersion = project.properties?.dockerTagVersion ?: 'latest'

    private boolean quiet = false

    private String tag = "${tagOrg}/${project.name}:${tagVersion}"

    private String dockerFile = 'src/main/docker/Dockerfile'

    private Set<Task> dependencies = Collections.emptySet()

    private Set<String> files = Collections.emptySet()

    private File resolvedDockerfile = null

    private Set<File> resolvedFiles = Collections.emptySet()

    private Map<String, String> buildArgs = new HashMap<>()

    /**
     * @param project
     */
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

    public void setTag(String tag) {
        this.tag = tag
    }

    public String getTag() {
        return tag
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

    /**
     * Utility method for validating values supplied by the user to the extension
     */
    void onAfterEvaluate() {

        Objects.requireNonNull(tag, "'tag' is required")

        this.resolvedDockerfile = project.file(dockerFile)
        if (!resolvedDockerfile.exists()) {
            throw new IllegalStateException("Dockerfile '${resolvedDockerfile}' does not exist")
        }

        Set<File> set = new HashSet<>();
        for (String file : files) {
            def resolvedFile = project.file(file)
            if (!resolvedFile.exists()) {
                throw new IllegalStateException("Specified file '${resolvedFile}' does not exist")
            }

            set.add(resolvedFile)
        }

        this.resolvedFiles = Collections.unmodifiableSet(set)
    }
}

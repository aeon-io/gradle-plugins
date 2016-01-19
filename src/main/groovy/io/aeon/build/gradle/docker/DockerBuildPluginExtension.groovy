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

    private final Map<String, String> buildArgs = new HashMap<>()

    private final Set<Task> dependencies = new HashSet<>()

    private final Set<String> files = new HashSet<>()

    private final Set<String> ulimitOpts = new HashSet<>()

    def boolean quiet = false

    def int cpuShares = 0

    def String cgroupParent = ''

    def int cpuPeriod = 0

    def int cpuQuota = 0

    def String cpuSetCpus = ''

    def String cpuSetMems = ''

    def boolean disableContentTrust = true

    def boolean forceRemove = false

    def String memory = ''

    def String memorySwap = ''

    def boolean noCache = false

    def boolean pull = false

    def boolean remove = true

    def String tag = "${tagOrg}/${project.name}:${tagVersion}"

    def String dockerFile = 'src/main/docker/Dockerfile'

    // ------------------------------------------------------------

    private File resolvedDockerfile = null

    private Set<File> resolvedFiles = Collections.emptySet()

    /**
     * @param project
     */
    public DockerBuildPluginExtension(Project project) {
        this.project = project
    }

    public void buildArg(String name, String value) {
        buildArgs.put(name.trim(), value.trim())
    }

    public void setDockerFile(String dockerFile) {
        this.dockerFile = dockerFile
    }

    public void dependsOn(Task... args) {
        Set<Task> set = new HashSet<>(Arrays.asList(args));
        dependencies.addAll(set)
    }

    public void files(String... args) {
        Set<String> set = new HashSet<>(Arrays.asList(args));
        files.addAll(set)
    }

    public void ulimit(String... args) {
        Set<String> set = new HashSet<>(Arrays.asList(args));
        ulimitOpts.addAll(set)
    }

    Set<Task> getDependencies() {
        return dependencies
    }

    File getResolvedDockerfile() {
        return resolvedDockerfile
    }

    Set<File> getResolvedFiles() {
        return resolvedFiles
    }

    Map<String, String> getBuildArgs() {
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

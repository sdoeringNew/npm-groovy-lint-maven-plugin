package com.github.mschieder.idea.formatter;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;

@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FormatMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;
    @Component
    private BuildPluginManager pluginManager;

    /**
     * Defines the error level where CLI will fail (return code = 1). error,warning,info or none. Each failure level includes the more critical ones.
     */
    @Parameter(property = "format.failOn", defaultValue = "error")
    private String failOn;

    /**
     * Automatically fix problems when possible
     */
    @Parameter(property = "format.fix", defaultValue = "false")
    private boolean fix;

    /**
     * Target containing the specific files, folders or patterns to lint
     */
    @Parameter(property = "format.targets", defaultValue = "${project.basedir}")
    private String targets;

    public void execute() throws MojoExecutionException {
        getLog().debug("failOn: " + failOn);
        getLog().debug("fix: " + fix);
        getLog().debug("targets: " + targets);

        try {
            final String installDirectory = Files.createTempDirectory("npm-groovy-lint").toAbsolutePath().toString();

            // install Node.js
            final MojoExecutor.ExecutionEnvironment environment = MojoExecutor.executionEnvironment(project, session, pluginManager);
            MojoExecutor.executeMojo(plugin("com.github.eirslett", "frontend-maven-plugin", "1.15.1"), "install-node-and-npm",
                    configuration(
                            element("nodeVersion", "v22.10.0"),
                            element("installDirectory", installDirectory)),
                    environment);

            // unzip pre-packaged npm-groovy-lint module
            Utils.unzipZippedFileFromResource(getLog(), Objects.requireNonNull(getClass().getResourceAsStream("/packaged-npm-groovy-lint.zip")), installDirectory);

            // install pre-packaged npm-groovy-lint module
            MojoExecutor.executeMojo(plugin("com.github.eirslett", "frontend-maven-plugin", "1.15.1"), "npm",
                    configuration(
                            element("arguments", "install npm-groovy-lint --prefer-offline"),
                            element("installDirectory", installDirectory)),
                    environment);

            // run npm-groovy-lint
            MojoExecutor.executeMojo(plugin("com.github.eirslett", "frontend-maven-plugin", "1.15.1"), "npx",
                    configuration(
                            element("arguments", "npm-groovy-lint --failon " + failOn + (fix ? " --fix " : " ") + targets),
                            element("installDirectory", installDirectory)),
                    environment);
        } catch (final IOException e) {
            throw new MojoExecutionException(e);
        }
    }
}
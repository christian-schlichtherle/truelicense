/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.proguard;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.String.join;
import static java.lang.System.getProperty;
import static java.util.Collections.addAll;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

@Mojo(name = "proguard", defaultPhase = PACKAGE, requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class ProGuardMojo extends AbstractMojo {

    /**
     * The dependencies of the project, not this plugin.
     */
    @Parameter(property = "project.artifacts", required = true, readonly = true)
    private Set<Artifact> artifacts;

    /**
     * The project build directory.
     */
    @Parameter(property = "project.build.directory", required = true, readonly = true)
    private File buildDirectory;

    /**
     * ProGuard filters for dependency jars.
     */
    @Parameter(defaultValue = "!module-info.class,!META-INF/**")
    private String dependencyFilter;

    /**
     * Add dependency jars as -libraryjars arguments?
     */
    @Parameter(defaultValue = "true")
    private boolean includeDependency;

    /**
     * Add dependency jars as -injars arguments?
     * Only considered if {@code includeDependency} is {@code true}.
     */
    @Parameter(defaultValue = "false")
    private boolean includeDependencyInjar;

    /**
     * The input jars or directories.
     */
    @Parameter(defaultValue = "${project.build.finalName}.${project.packaging}(!module-info.class,!META-INF/maven/**)")
    private List<String> injars;

    /**
     * Additional library jars or directories, e.g.
     * {@code <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)}.
     */
    @Parameter
    private List<String> libraryjars;

    /**
     * ProGuard options.
     */
    @Parameter
    private String[] options;

    /**
     * The output jars or directories.
     */
    @Parameter(defaultValue = "${project.build.finalName}-guarded.${project.packaging}(!META-INF/maven/**)")
    private List<String> outjars;

    /**
     * The project packaging type.
     */
    @Parameter(property = "project.packaging", required = true, readonly = true)
    private String packaging;

    /**
     * The dependencies of this plugin, not the project.
     */
    @Parameter(property = "plugin.artifacts", required = true, readonly = true)
    private List<Artifact> pluginArtifacts;

    /**
     * The Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Disable the plugin execution?
     */
    @Parameter(property = "proguard.skip", defaultValue = "false")
    private boolean skip;

    @Component
    private MavenProjectHelper helper;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("ProGuard execution is skipped.");
        } else {
            try {
                executeProGuard();
                attachOutjars();
            } catch (MojoFailureException | RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new MojoFailureException(e.toString(), e);
            }
        }
    }

    private void executeProGuard() throws MojoFailureException, IOException, InterruptedException {
        final List<String> c = new LinkedList<>();
        c.add(getProperty("java.home") + "/bin/java");
        c.add("-jar");
        c.add(proGuardFile().getAbsolutePath());
        c.addAll(commandLineArgs());
        getLog().info("Executing ProGuard: " + join(" ", c));
        final Process p = new ProcessBuilder(c).directory(buildDirectory).inheritIO().start();
        p.waitFor();
        if (0 != p.exitValue()) {
            throw new MojoFailureException("ProGuard execution failed.");
        }
    }

    private File proGuardFile() throws MojoFailureException {
        return pluginArtifacts
                .stream()
                .filter(a -> "proguard-base".equals(a.getArtifactId()))
                .findFirst()
                .map(Artifact::getFile)
                .orElseThrow(() -> new MojoFailureException("ProGuard not found."));
    }

    private List<String> commandLineArgs() throws MojoFailureException {
        return new Object() {
            final List<String> args = new LinkedList<>();

            {
                addInJars();
                addDependencyJars();
                addLibraryJars();
                addOutJars();
                addOptions();
            }

            void addInJars() throws MojoFailureException {
                injars.forEach(injar -> {
                    args.add("-injars");
                    args.add(injar);
                });
            }

            void addDependencyJars() {
                if (includeDependency) {
                    final String option = includeDependencyInjar ? "-injars" : "-libraryjars";
                    final String filter = null == dependencyFilter ? "" : "(" + dependencyFilter + ")";
                    artifacts.forEach(artifact -> {
                        final String path = artifact.getFile().getAbsolutePath();
                        args.add(option);
                        args.add(path + filter);
                    });
                }
            }

            void addLibraryJars() {
                if (null != libraryjars && libraryjars.size() > 0) {
                    libraryjars.forEach(libraryjar -> {
                        args.add("-libraryjars");
                        args.add(libraryjar);
                    });
                } else {
                    final File rtJar = new File(getProperty("java.home"), "lib/rt.jar");
                    args.add("-libraryjars");
                    args.add("<java.home>/" + (rtJar.isFile() ? "lib" : "jmods(!**.jar;!module-info.class)"));
                }
            }

            void addOutJars() throws MojoFailureException {
                outjars.forEach(outjar -> {
                    args.add("-outjars");
                    args.add(outjar);
                });
            }

            void addOptions() {
                if (null != options) {
                    addAll(args, options);
                }
            }
        }.args;
    }

    private void attachOutjars() {
        outjars.forEach(outjar -> {
            final int i = outjar.lastIndexOf('(');
            final String path = -1 == i ? outjar : outjar.substring(0, i).trim();
            helper.attachArtifact(project, packaging, "guarded", new File(buildDirectory, path));
        });
    }
}

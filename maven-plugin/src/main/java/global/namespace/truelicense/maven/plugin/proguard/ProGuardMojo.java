/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.proguard;

import global.namespace.truelicense.build.tasks.commons.Task;
import global.namespace.truelicense.build.tasks.proguard.ProGuardTask;
import global.namespace.truelicense.maven.plugin.commons.BasicMojo;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static global.namespace.neuron.di.java.Incubator.wire;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

/**
 * A goal for the general obfuscation of byte code of Java class files using ProGuard.
 */
@SuppressWarnings("unused")
@Mojo(name = "proguard", defaultPhase = PACKAGE, requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public final class ProGuardMojo extends BasicMojo {

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
     * This dependency provider method is used to wire {@link ProGuardTask}.
     *
     * @see #task()
     */
    Path buildDirectory() {
        return buildDirectory.toPath();
    }

    /**
     * This dependency provider method is used to wire {@link ProGuardTask}.
     *
     * @see #task()
     */
    Set<Path> dependencies() {
        return artifacts.stream().map(a -> a.getFile().toPath()).collect(toSet());
    }

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
     * This dependency provider method is used to wire {@link ProGuardTask}.
     *
     * @see #task()
     */
    List<String> libraryjars() {
        final List<String> l = libraryjars;
        return null != l ? l : (libraryjars = emptyList());
    }

    /**
     * ProGuard options.
     */
    @Parameter
    private List<String> options;

    /**
     * This dependency provider method is used to wire {@link ProGuardTask}.
     *
     * @see #task()
     */
    List<String> options() {
        final List<String> o = options;
        return null != o ? o : (options = emptyList());
    }

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
     * This dependency provider method is used to wire {@link ProGuardTask}.
     *
     * @see #task()
     */
    Path proGuardJar() {
        return pluginArtifacts
                .stream()
                .filter(a -> "proguard-base".equals(a.getArtifactId()))
                .findFirst()
                .map(a -> a.getFile().toPath())
                .orElseThrow(() -> new IllegalStateException("The ProGuard JAR is missing on the plugin class path."));
    }

    /**
     * The Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Component
    private MavenProjectHelper helper;

    @Override
    protected final Task task() {
        return wire(ProGuardTask.class).using(this);
    }

    @Override
    protected void postExecute() {
        outjars.forEach(outjar -> {
            final int i = outjar.lastIndexOf('(');
            final String path = -1 == i ? outjar : outjar.substring(0, i).trim();
            helper.attachArtifact(project, packaging, "guarded", new File(buildDirectory, path));
        });
    }
}

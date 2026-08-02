/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.proguard;

import global.namespace.truelicense.build.tasks.commons.Logger;
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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
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
     * Returns the project build directory, which is where ProGuard runs.
     */
    Path buildDirectory() {
        return buildDirectory.toPath();
    }

    /**
     * Returns the paths of the project's dependencies.
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
     * The build's final name.
     */
    @Parameter(property = "project.build.finalName", required = true, readonly = true)
    private String finalName;

    /**
     * The input jars or directories.
     * <p>
     * Defaults to the project artifact. The default cannot be a {@code defaultValue}: Maven splits one into list
     * elements at every comma, which would turn the filter below into a second {@code -injars} argument and make
     * ProGuard fail to parse its own command line.
     */
    @Parameter
    private List<String> injars;

    /**
     * Returns the input jars or directories, defaulting to the project artifact.
     */
    List<String> injars() {
        final List<String> i = injars;
        return null != i ? i : (injars = singletonList(defaultInjar(finalName, packaging)));
    }

    static String defaultInjar(final String finalName, final String packaging) {
        return finalName + "." + packaging + "(!module-info.class,!META-INF/maven/**)";
    }

    /**
     * Additional library jars or directories, e.g.
     * {@code <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)}.
     */
    @Parameter
    private List<String> libraryjars;

    /**
     * Returns the additional library jars or directories, defaulting to none.
     */
    List<String> libraryjars() {
        final List<String> l = libraryjars;
        return null != l ? l : (libraryjars = emptyList());
    }

    /**
     * The maximum heap size of the ProGuard process, e.g. {@code 2g}. Unset leaves the forked JVM its default, which
     * is where a large program runs out of memory first.
     */
    @Parameter(property = "truelicense.proguard.maxHeapSize")
    private String maxHeapSize;

    /**
     * ProGuard options.
     */
    @Parameter
    private List<String> options;

    /**
     * Returns the ProGuard options, defaulting to none.
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
     * Returns the class path to run ProGuard from.
     * <p>
     * Passes this plugin's entire class path, not just the ProGuard JAR: {@code com.guardsquare:proguard-base}
     * declares its dependencies rather than shading them, so ProGuard needs them alongside it. Declare
     * {@code proguard-base} in this plugin's {@code <dependencies>} and Maven resolves the rest.
     */
    List<Path> proGuardClassPath() {
        if (pluginArtifacts.stream().noneMatch(a -> "proguard-base".equals(a.getArtifactId()))) {
            throw new IllegalStateException("The ProGuard JAR is missing on the plugin class path.");
        }
        return pluginArtifacts.stream().map(a -> a.getFile().toPath()).collect(toList());
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
        return new ProGuardTask() {

            @Override
            public Logger logger() {
                return ProGuardMojo.this.logger();
            }

            @Override
            public Path buildDirectory() {
                return ProGuardMojo.this.buildDirectory();
            }

            @Override
            public Set<Path> dependencies() {
                return ProGuardMojo.this.dependencies();
            }

            @Override
            public String dependencyFilter() {
                return dependencyFilter;
            }

            @Override
            public boolean includeDependency() {
                return includeDependency;
            }

            @Override
            public boolean includeDependencyInjar() {
                return includeDependencyInjar;
            }

            @Override
            public List<String> injars() {
                return ProGuardMojo.this.injars();
            }

            @Override
            public List<String> libraryjars() {
                return ProGuardMojo.this.libraryjars();
            }

            @Override
            public String maxHeapSize() {
                return maxHeapSize;
            }

            @Override
            public List<String> options() {
                return ProGuardMojo.this.options();
            }

            @Override
            public List<String> outjars() {
                return outjars;
            }

            @Override
            public List<Path> proGuardClassPath() {
                return ProGuardMojo.this.proGuardClassPath();
            }
        };
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

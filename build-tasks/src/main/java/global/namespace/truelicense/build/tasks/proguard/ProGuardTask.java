/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.proguard;

import global.namespace.neuron.di.java.Neuron;
import global.namespace.truelicense.build.tasks.commons.AbstractTask;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static global.namespace.neuron.di.java.CachingStrategy.NOT_THREAD_SAFE;
import static java.lang.String.join;
import static java.lang.System.getProperty;
import static java.nio.file.Files.isRegularFile;

/**
 * Obfuscates the byte code of Java class files using ProGuard..
 */
@SuppressWarnings("WeakerAccess")
@Neuron(cachingStrategy = NOT_THREAD_SAFE)
public abstract class ProGuardTask extends AbstractTask {

    /**
     * The project build directory.
     */
    public abstract Path buildDirectory();

    private List<String> commandLineArgs() {
        return new Object() {

            final List<String> args = new LinkedList<>();

            {
                addInJars();
                addDependencyJars();
                addLibraryJars();
                addOutJars();
                addOptions();
            }

            void addInJars() {
                injars().forEach(injar -> {
                    args.add("-injars");
                    args.add(injar);
                });
            }

            void addDependencyJars() {
                if (includeDependency()) {
                    final String option = includeDependencyInjar() ? "-injars" : "-libraryjars";
                    final String filter = null == dependencyFilter() ? "" : "(" + dependencyFilter() + ")";
                    dependencies().forEach(artifact -> {
                        final String path = artifact.toAbsolutePath().toString();
                        args.add(option);
                        args.add(path + filter);
                    });
                }
            }

            void addLibraryJars() {
                if (libraryjars().size() > 0) {
                    libraryjars().forEach(libraryjar -> {
                        args.add("-libraryjars");
                        args.add(libraryjar);
                    });
                } else {
                    final Path rtJar = Paths.get(getProperty("java.home"), "lib/rt.jar");
                    args.add("-libraryjars");
                    args.add("<java.home>/" + (isRegularFile(rtJar) ? "lib" : "jmods(!**.jar;!module-info.class)"));
                }
            }

            void addOutJars() {
                outjars().forEach(outjar -> {
                    args.add("-outjars");
                    args.add(outjar);
                });
            }

            void addOptions() {
                args.addAll(options());
            }
        }.args;
    }

    /**
     * The dependencies of the project.
     */
    public abstract Set<Path> dependencies();

    /**
     * ProGuard filters for dependency jars.
     */
    public abstract String dependencyFilter();

    /**
     * Add dependency jars as -libraryjars arguments?
     */
    public abstract boolean includeDependency();

    /**
     * Add dependency jars as -injars arguments?
     * Only considered if {@code includeDependency} is {@code true}.
     */
    public abstract boolean includeDependencyInjar();

    /**
     * The input jars or directories.
     */
    public abstract List<String> injars();

    /**
     * Additional library jars or directories, e.g.
     * {@code <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)}.
     */
    public abstract List<String> libraryjars();

    /**
     * ProGuard options.
     */
    public abstract List<String> options();

    /**
     * The output jars or directories.
     */
    public abstract List<String> outjars();

    /**
     * The shaded JAR containing the main class for ProGuard.
     */
    public abstract Path proGuardJar();

    @Override
    public final void execute() throws Exception {
        final List<String> c = new LinkedList<>();
        c.add(getProperty("java.home") + "/bin/java");
        c.add("-jar");
        c.add(proGuardJar().toAbsolutePath().toString());
        c.addAll(commandLineArgs());
        logger().info("Executing ProGuard: " + join(" ", c));
        final Process p = new ProcessBuilder(c).directory(buildDirectory().toFile()).inheritIO().start();
        p.waitFor();
        if (0 != p.exitValue()) {
            throw new Exception("ProGuard execution failed.");
        }
    }
}

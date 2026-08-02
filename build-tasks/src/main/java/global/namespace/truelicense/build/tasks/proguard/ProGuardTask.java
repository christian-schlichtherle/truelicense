/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.proguard;

import global.namespace.truelicense.build.tasks.commons.AbstractTask;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.String.join;
import static java.lang.System.getProperty;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.util.stream.Collectors.joining;

/**
 * Obfuscates the byte code of Java class files using ProGuard..
 */
@SuppressWarnings("WeakerAccess")
public abstract class ProGuardTask extends AbstractTask {

    private static final String MAIN_CLASS = "proguard.ProGuard";

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
                    final Path javaHome = Paths.get(getProperty("java.home"));
                    args.add("-libraryjars");
                    if (isRegularFile(javaHome.resolve("lib/rt.jar"))) {
                        args.add("<java.home>/lib");
                        args.add("-libraryjars");
                        args.add("<java.home>/lib/ext");
                    } else if (isDirectory(javaHome.resolve("jmods"))) {
                        args.add("<java.home>/jmods(!**.jar;!module-info.class)");
                    } else {
                        // Some JDKs ship no jmods, e.g. Temurin 25. ProGuard then reads the runtime image from
                        // java.home itself, which it must not do when jmods is present: it would find every class
                        // twice, once there and once in lib/modules.
                        args.add("<java.home>");
                    }
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
     * The maximum heap size of the ProGuard process, e.g. {@code 2g}, or {@code null} to leave the forked JVM its
     * default. ProGuard holds the whole program in memory, so a large one needs more than the default heap.
     */
    public abstract String maxHeapSize();

    /**
     * The class path to run ProGuard from. Must contain ProGuard and everything it depends on: only
     * {@code net.sf.proguard:proguard-base} is a shaded, runnable JAR, whereas {@code com.guardsquare:proguard-base}
     * declares its dependencies instead, so ProGuard is started by class path and main class rather than by
     * {@code -jar}.
     */
    public abstract List<Path> proGuardClassPath();

    /**
     * The command line which {@link #execute()} runs.
     */
    public final List<String> commandLine() {
        final List<String> c = new LinkedList<>();
        c.add(getProperty("java.home") + "/bin/java");
        final String heap = maxHeapSize();
        if (null != heap && !heap.isEmpty()) {
            c.add("-Xmx" + heap);
        }
        c.add("-cp");
        c.add(proGuardClassPath()
                .stream()
                .map(p -> p.toAbsolutePath().toString())
                .collect(joining(File.pathSeparator)));
        c.add(MAIN_CLASS);
        c.addAll(commandLineArgs());
        return c;
    }

    @Override
    public final void execute() throws Exception {
        final List<String> c = commandLine();
        logger().info("Executing ProGuard: " + join(" ", c));
        final Process p = new ProcessBuilder(c).directory(buildDirectory().toFile()).inheritIO().start();
        p.waitFor();
        if (0 != p.exitValue()) {
            throw new Exception("ProGuard execution failed.");
        }
    }
}

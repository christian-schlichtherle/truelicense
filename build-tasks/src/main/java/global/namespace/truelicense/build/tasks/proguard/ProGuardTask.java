/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.proguard;

import global.namespace.truelicense.build.tasks.commons.AbstractTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import static java.lang.String.join;
import static java.lang.System.getProperty;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.joining;

/**
 * Obfuscates the byte code of Java class files using ProGuard..
 */
@SuppressWarnings("WeakerAccess")
public abstract class ProGuardTask extends AbstractTask {

    private static final String MAIN_CLASS = "proguard.ProGuard";

    private static final String PLATFORM_CLASSES_JAR = "truelicense-proguard-platform-classes.jar";

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
                    args.add("-libraryjars");
                    switch (platformLayout()) {
                        case LEGACY:
                            args.add("<java.home>/lib");
                            args.add("-libraryjars");
                            args.add("<java.home>/lib/ext");
                            break;
                        case JMODS:
                            args.add("<java.home>/jmods(!**.jar;!module-info.class)");
                            break;
                        default:
                            args.add(platformClassesJar().toAbsolutePath().toString());
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
     * How this JDK exposes its platform classes to ProGuard.
     */
    enum PlatformLayout {

        /** Java 8: {@code lib/rt.jar} plus {@code lib/ext}. */
        LEGACY,

        /** A modular JDK that ships {@code jmods}, which ProGuard reads directly. */
        JMODS,

        /** A modular JDK that ships no {@code jmods}, leaving only the runtime image. */
        RUNTIME_IMAGE
    }

    static PlatformLayout platformLayout() {
        final Path javaHome = Paths.get(getProperty("java.home"));
        if (isRegularFile(javaHome.resolve("lib/rt.jar"))) {
            return PlatformLayout.LEGACY;
        } else if (isDirectory(javaHome.resolve("jmods"))) {
            return PlatformLayout.JMODS;
        } else {
            return PlatformLayout.RUNTIME_IMAGE;
        }
    }

    /**
     * Where {@link #stagePlatformClasses()} puts the platform classes of a JDK that ships no {@code jmods}.
     */
    final Path platformClassesJar() {
        return buildDirectory().resolve(PLATFORM_CLASSES_JAR);
    }

    /**
     * Copies the platform's class files out of the runtime image into {@link #platformClassesJar()}.
     * <p>
     * Some JDKs ship no {@code jmods} - Temurin 25 is one - and then nothing under {@code java.home} is a class path
     * entry ProGuard can read: {@code lib/modules} is a jimage, and proguard-core reads jars, directories and
     * {@code .jmod} files only. Passing {@code java.home} itself is what this replaces; ProGuard accepts it, finds no
     * class in it, and reports every reference into the JDK as unresolved - down to
     * {@code can't find superclass or interface java.lang.Object} - which then fails the run on warnings.
     * <p>
     * The {@code jrt} file system is readable because this task runs on the very JDK whose classes ProGuard needs.
     * Entries are written without their module segment, so the jar looks like an ordinary class path root; a class
     * present in two modules keeps whichever copy comes first, which the JDK's own modules do not do anyway.
     */
    final void stagePlatformClasses() throws IOException {
        final Path jar = platformClassesJar();
        createDirectories(jar.getParent());
        final FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(newOutputStream(jar)))) {
            // These classes are read once and thrown away, so compressing them only costs time.
            zip.setLevel(Deflater.NO_COMPRESSION);
            try (DirectoryStream<Path> modules = newDirectoryStream(jrt.getPath("/modules"))) {
                for (final Path module : modules) {
                    try (Stream<Path> entries = walk(module)) {
                        for (final Path entry : (Iterable<Path>) entries::iterator) {
                            final String name = module.relativize(entry).toString();
                            if (!name.endsWith(".class") || name.equals("module-info.class")) {
                                continue;
                            }
                            try {
                                zip.putNextEntry(new ZipEntry(name));
                                copy(entry, zip);
                                zip.closeEntry();
                            } catch (ZipException alreadyPresent) {
                                logger().debug("Skipping duplicate platform class: " + name);
                            }
                        }
                    }
                }
            }
        }
    }

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
        // Staged here rather than while building the command line, so that inspecting the command line stays free of
        // side effects - and of 60-odd MB of I/O.
        if (libraryjars().isEmpty() && PlatformLayout.RUNTIME_IMAGE == platformLayout()) {
            stagePlatformClasses();
        }
        final List<String> c = commandLine();
        logger().info("Executing ProGuard: " + join(" ", c));
        final Process p = new ProcessBuilder(c).directory(buildDirectory().toFile()).inheritIO().start();
        p.waitFor();
        if (0 != p.exitValue()) {
            throw new Exception("ProGuard execution failed.");
        }
    }
}

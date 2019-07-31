/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.generation;

import global.namespace.truelicense.build.tasks.commons.Task;
import global.namespace.truelicense.build.tasks.generation.PathSet;
import global.namespace.truelicense.maven.plugin.commons.BasicMojo;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static global.namespace.neuron.di.java.Incubator.wire;
import static java.lang.Character.toUpperCase;
import static java.util.stream.Collectors.toList;

/**
 * Generates main or test source files by merging a set of <a href="http://velocity.apache.org">Apache Velocity</a>
 * template files with all properties in the Maven POM.
 */
@SuppressWarnings("unused")
public abstract class GenerateSourcesMojo extends BasicMojo {

    /**
     * The character set to use for reading the template files and writing the
     * output files.
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    /**
     * The properties to put into the Velocity context so that they can get referenced in template files.
     */
    @Parameter
    private Properties properties;

    /**
     * This dependency provider method is used to wire
     * {@link global.namespace.truelicense.build.tasks.generation.GenerateSourcesTask}.
     *
     * @see #task()
     */
    Properties properties() {
        final Properties pp = new Properties(project.getProperties());
        final Properties p = properties;
        if (null != p) {
            pp.putAll(p);
        }
        return pp;
    }

    abstract String stripPrefix();

    /**
     * The suffix to strip from the names of the output files.
     */
    @Parameter(property = "truelicense.generate.stripSuffix", defaultValue = ".vtl")
    private String stripSuffix;

    @Override
    protected final Task task() {
        return wire(GenerateSourcesMavenTask.class).using(this);
    }

    /**
     * The list of {@link FileSet}s with template files to process.
     * Defaults to all files in the directory {@code ${stripPrefix}java} with the suffix {@code ${stripSuffix}},
     * e.g. all files in the directory {@code src/main/java} with the suffix {@code .vtl}.
     */
    @Parameter
    private List<FileSet> templateSets;

    /**
     * This dependency provider method is used to wire
     * {@link global.namespace.truelicense.build.tasks.generation.GenerateSourcesTask}.
     *
     * @see #task()
     */
    List<PathSet> templateSets() {
        List<FileSet> ss = templateSets;
        if (null == ss || ss.isEmpty()) {
            final FileSet s = new FileSet();
            s.setDirectory(stripPrefix() + "java");
            s.addInclude("**/*" + stripSuffix);
            ss = Collections.singletonList(s);
        }
        return ss
                .stream()
                .map(fs -> wire(PathSet.class)
                        .bind(PathSet::directory).to(Paths.get(fs.getDirectory()))
                        .using(fs, GenerateSourcesMojo::getterName))
                .collect(toList());
    }

    private static String getterName(final Method m) {
        final String n = m.getName();
        // Naive, but sufficient for this case:
        return "get" + toUpperCase(n.charAt(0)) + n.substring(1);
    }
}

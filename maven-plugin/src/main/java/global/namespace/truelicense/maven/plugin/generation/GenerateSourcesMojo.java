/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.generation;

import global.namespace.truelicense.build.tasks.commons.Logger;
import global.namespace.truelicense.build.tasks.commons.Task;
import global.namespace.truelicense.build.tasks.generation.GenerateSourcesStrategy;
import global.namespace.truelicense.build.tasks.generation.PathSet;
import global.namespace.truelicense.maven.plugin.commons.BasicMojo;
import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
     * Returns the properties to put into the Velocity context, with the configured properties overriding the
     * project's.
     */
    Properties properties() {
        final Properties pp = new Properties(project.getProperties());
        final Properties p = properties;
        if (null != p) {
            pp.putAll(p);
        }
        return pp;
    }

    /**
     * Returns the directory path where the generated source files will be stored.
     */
    abstract Path outputDirectory();

    /**
     * Returns the strategy which decides if main or test sources are to be generated.
     */
    abstract GenerateSourcesStrategy strategy();

    abstract String stripPrefix();

    /**
     * The suffix to strip from the names of the output files.
     */
    @Parameter(property = "truelicense.generate.stripSuffix", defaultValue = ".vtl")
    private String stripSuffix;

    @Override
    protected final Task task() {
        return new GenerateSourcesMavenTask() {

            @Override
            public Logger logger() {
                return GenerateSourcesMojo.this.logger();
            }

            @Override
            public String encoding() {
                return encoding;
            }

            @Override
            public GenerateSourcesStrategy strategy() {
                return GenerateSourcesMojo.this.strategy();
            }

            @Override
            public Path outputDirectory() {
                return GenerateSourcesMojo.this.outputDirectory();
            }

            @Override
            MavenProject project() {
                return project;
            }

            @Override
            public Properties properties() {
                return GenerateSourcesMojo.this.properties();
            }

            @Override
            public String stripPrefix() {
                return GenerateSourcesMojo.this.stripPrefix();
            }

            @Override
            public String stripSuffix() {
                return stripSuffix;
            }

            @Override
            public List<PathSet> templateSets() {
                return GenerateSourcesMojo.this.templateSets();
            }
        };
    }

    /**
     * The list of {@link FileSet}s with template files to process.
     * Defaults to all files in the directory {@code ${stripPrefix}java} with the suffix {@code ${stripSuffix}},
     * e.g. all files in the directory {@code src/main/java} with the suffix {@code .vtl}.
     */
    @Parameter
    private List<FileSet> templateSets;

    /**
     * Returns the template file sets to process, defaulting to all files in the directory {@code ${stripPrefix}java}
     * with the suffix {@code ${stripSuffix}}.
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
                .map(fs -> new PathSet(Paths.get(fs.getDirectory()), fs.getIncludes(), fs.getExcludes()))
                .collect(toList());
    }
}

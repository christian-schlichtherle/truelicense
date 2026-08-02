/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.generation;

import global.namespace.truelicense.build.tasks.generation.GenerateSourcesStrategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Path;

import static global.namespace.truelicense.build.tasks.generation.GenerateSourcesStrategy.mainSources;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

/**
 * Generates main source files by merging a set of
 * <a href="http://velocity.apache.org">Apache Velocity</a> template files with
 * all properties in the Maven POM.
 */
@Mojo(name = "generate-main-sources", defaultPhase = GENERATE_SOURCES)
public final class GenerateMainSourcesMojo extends GenerateSourcesMojo {

    /**
     * The directory path where the generated source files will be stored.
     */
    @Parameter(property = "truelicense.generate.main.outputDirectory", defaultValue = "${project.build.directory}/generated-sources")
    private File outputDirectory;

    /**
     * Returns the directory path where the generated source files will be stored.
     */
    @Override
    Path outputDirectory() {
        return outputDirectory.toPath();
    }

    /**
     * Returns the strategy which generates main sources.
     */
    @Override
    GenerateSourcesStrategy strategy() {
        return mainSources;
    }

    /**
     * The prefix to strip from the directory path of each file set before
     * appending it to the output directory path.
     */
    @Parameter(property = "truelicense.generate.main.stripPrefix", defaultValue = "src/main/")
    private String stripPrefix;

    /**
     * Returns the prefix to strip from the directory path of each file set before appending it to the output
     * directory path.
     */
    @Override
    String stripPrefix() {
        return stripPrefix;
    }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.generation;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

/**
 * Generates main source files by merging a set of
 * <a href="http://velocity.apache.org">Apache Velocity</a> template files with
 * all properties in the Maven POM.
 *
 * @since TrueLicense 2.4
 * @author Christian Schlichtherle
 */
@Mojo(name = "generate-main-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public final class GenerateMainSourcesMojo extends GenerateSourcesMojo {

    /**
     * The list of {@link FileSet}s with template files to process.
     * Defaults to all files in the directory <code>${stripPrefix}java}</code>
     * with the suffix <code>${stripSuffix}}</code>, e.g. all files in the
     * directory {@code src/main/java} with the suffix {@code .vtl}.
     */
    @Parameter
    private List<FileSet> templateSets;

    /** The directory path where the generated source files will be stored. */
    @Parameter(property = "truelicense.generate.main.outputDirectory", defaultValue = "${project.build.directory}/generated-sources")
    private File outputDirectory;

    /**
     * The prefix to strip from the directory path of each file set before
     * appending it to the output directory path.
     */
    @Parameter(property = "truelicense.generate.main.stripPrefix", defaultValue = "src/main/")
    private String stripPrefix;

    @Override
    protected GenerateSourcesStrategy generateSourcesStrategy() {
        return GenerateSourcesStrategy.mainSources;
    }

    @Override
    protected List<FileSet> templateSets() { return templateSets; }

    @Override
    protected File outputDirectory() { return outputDirectory; }

    @Override
    protected String stripPrefix() { return stripPrefix; }
}

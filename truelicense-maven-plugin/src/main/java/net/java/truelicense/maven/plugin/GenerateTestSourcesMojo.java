/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates test source files by merging a set of
 * <a href="http://velocity.apache.org">Apache Velocity</a> template files with
 * all properties in the Maven POM.
 *
 * @since TrueLicense 2.4
 * @author Christian Schlichtherle
 */
@Mojo(name = "generate-test-sources", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public final class GenerateTestSourcesMojo extends GenerateSourcesMojo {

    /**
     * The list of {@link FileSet}s with template files to process.
     * Defaults to all files in {@code src/test/java} with a {@code vtl}
     * extension.
     */
    @Parameter
    private List<FileSet> templateSets;

    /** The directory path where the generated source files will be stored. */
    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources")
    private File outputDirectory;

    /**
     * The prefix to strip from the directory path of each file set before
     * appending it to the output directory path.
     */
    @Parameter(defaultValue = "src/test/")
    private String stripPrefix;

    @Override
    protected GenerateSourcesStrategy generateSourcesStrategy() {
        return GenerateSourcesStrategy.testSources;
    }

    @Override
    protected List<FileSet> templateSets() {
        if (null == templateSets) {
            final FileSet templateSet = new FileSet();
            templateSet.setDirectory("src/test/java");
            templateSet.addInclude("**/*.vtl");
            templateSets = new LinkedList<FileSet>();
            templateSets.add(templateSet);
        }
        return templateSets;
    }

    @Override
    protected File outputDirectory() { return outputDirectory; }

    @Override
    protected String stripPrefix() { return stripPrefix; }
}

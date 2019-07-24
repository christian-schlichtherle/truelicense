/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.maven.plugin.generation;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
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
     * Defaults to all files in the directory <code>${stripPrefix}java}</code>
     * with the suffix <code>${stripSuffix}}</code>, e.g. all files in the
     * directory {@code src/test/java} with the suffix {@code .vtl}.
     */
    @Parameter
    private List<FileSet> templateSets;

    /** The directory path where the generated source files will be stored. */
    @Parameter(property = "truelicense.generate.test.outputDirectory", defaultValue = "${project.build.directory}/generated-test-sources")
    private File outputDirectory;

    /**
     * The prefix to strip from the directory path of each file set before
     * appending it to the output directory path.
     */
    @Parameter(property = "truelicense.generate.test.stripPrefix", defaultValue = "src/test/")
    private String stripPrefix;

    @Override
    protected GenerateSourcesStrategy generateSourcesStrategy() {
        return GenerateSourcesStrategy.testSources;
    }

    @Override
    protected List<FileSet> templateSets() { return templateSets; }

    @Override
    protected File outputDirectory() { return outputDirectory; }

    @Override
    protected String stripPrefix() { return stripPrefix; }
}

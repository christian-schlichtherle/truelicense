/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.generation;

import global.namespace.neuron.di.java.Caching;
import global.namespace.truelicense.build.tasks.commons.AbstractTask;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.config.ConfigurationUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static global.namespace.neuron.di.java.CachingStrategy.DISABLED;
import static java.lang.String.join;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newOutputStream;
import static org.apache.velocity.runtime.RuntimeConstants.FILE_RESOURCE_LOADER_PATH;

/**
 * Generates main or test source files by merging a set of <a href="http://velocity.apache.org">Apache Velocity</a>
 * template files with all properties in the Maven POM.
 */
@SuppressWarnings("WeakerAccess")
public abstract class GenerateSourcesTask extends AbstractTask {

    private static final JavaTool javaTool = new JavaTool();
    private static final ScalaTool scalaTool = new ScalaTool();

    @Caching(DISABLED)
    public Context context() {
        final Context c = toolManager().createContext();
        c.put("java", javaTool);
        c.put("scala", scalaTool);
        properties().stringPropertyNames().forEach(key -> c.put(key, properties().getProperty(key)));
        return c;
    }

    /**
     * The character set to use for reading the template files and writing the output files.
     */
    public abstract String encoding();

    /**
     * The strategy which decides if main or test sources are to be generated.
     */
    public abstract GenerateSourcesStrategy strategy();

    /**
     * The directory path where the generated source files will be stored.
     */
    public abstract Path outputDirectory();

    public abstract Path projectDirectory();

    /**
     * A set of properties to put into the Velocity context so that they can get referenced in template files.
     */
    public abstract Properties properties();

    /**
     * The prefix to strip from the directory path of each file set before
     * appending it to the output directory path.
     */
    public abstract String stripPrefix();

    /**
     * The suffix to strip from the names of the output files.
     */
    public abstract String stripSuffix();

    /**
     * The list of {@link PathSet}s with template files to process.
     * Defaults to all files in the directory {@code ${stripPrefix}java} with the suffix {@code ${stripSuffix}},
     * e.g. all files in the directory {@code src/main/java} with the suffix {@code .vtl}.
     */
    public abstract List<PathSet> templateSets();

    @Caching
    ToolManager toolManager() {
        final ToolManager m = new ToolManager();
        m.configure(ConfigurationUtils.getDefaultTools());
        return m;
    }

    @Caching
    VelocityEngine velocityEngine() {
        final VelocityEngine e = new VelocityEngine();
        e.setProperty(FILE_RESOURCE_LOADER_PATH, projectDirectory().toString());
        e.init();
        return e;
    }

    @Override
    public final void execute() throws Exception {
        for (PathSet pathSet : templateSets()) {
            new TemplateSet() {

                PathSet templates() {
                    return pathSet;
                }
            }.processDirectory();
        }
    }

    // TODO: Refactor this into a property or better yet, move it into `FileSet`!
    protected abstract List<String> list(Path directory, List<String> includes, List<String> excludes) throws IOException;

    // TODO: Refactor this into a property!
    protected abstract void addCompileSourceRoot(String path);

    // TODO: Refactor this into a property!
    protected abstract void addTestCompileSourceRoot(String path);

    private abstract class TemplateSet implements SourceGenerator {

        Path mergeDirectory;

        abstract PathSet templates();

        void processDirectory() throws Exception {
            final Path templateDirectory = resolveWithProjectDirectory(templateDirectoryPath());
            final List<String> paths = list(templateDirectory, templates().includes(), templates().excludes());
            if (paths.isEmpty()) {
                logger().warn("No such file or empty directory: " + templates());
            } else {
                logger().info("Template directory: " + templateDirectory);
                logger().info("Merge directory: " + mergeDirectory());
                for (String path : paths) {
                    processFile(path);
                }
                strategy().generateSourcesUsing(this);
            }
        }

        void processFile(final String templatePath) throws Exception {
            final Path expandedTemplatePath = resolveWithTemplateDirectory(templatePath);
            final Path mergeFile = resolveWithMergeDirectory(stripSuffix(templatePath));
            if (logger().isDebugEnabled()) {
                final Path templateFile = resolveWithProjectDirectory(expandedTemplatePath);
                logger().debug("Template file: " + templateFile);
                logger().debug("Merge file: " + mergeFile);
            }
            createDirectories(mergeFile.getParent());
            final Context c = context();
            logger().debug(() -> "Populated a new Velocity context with the following keys: " + join(", ", c.getKeys()));
            try (OutputStream out = newOutputStream(mergeFile);
                 Writer writer = new OutputStreamWriter(out, encoding())) {
                velocityEngine().getTemplate(expandedTemplatePath.toString(), encoding()).merge(c, writer);
            }
        }

        Path resolveWithMergeDirectory(String path) {
            return mergeDirectory().resolve(path);
        }

        Path resolveWithOutputDirectory(String path) {
            return outputDirectory().resolve(path);
        }

        Path resolveWithProjectDirectory(Path path) {
            return projectDirectory().resolve(path);
        }

        Path resolveWithTemplateDirectory(String path) {
            return templateDirectoryPath().resolve(path);
        }

        @Override
        public void addMergeDirectoryToCompileSourceRoot() {
            addCompileSourceRoot(mergeDirectoryPath());
        }

        @Override
        public void addMergeDirectoryToTestCompileSourceRoot() {
            addTestCompileSourceRoot(mergeDirectoryPath());
        }

        String mergeDirectoryPath() {
            return mergeDirectory().toString();
        }

        Path mergeDirectory() {
            final Path d = mergeDirectory;
            return null != d ? d : (this.mergeDirectory = mergeDirectory0());
        }

        Path mergeDirectory0() {
            return resolveWithOutputDirectory(stripPrefix(templateDirectoryPath().toString()));
        }

        Path templateDirectoryPath() {
            return templates().directory();
        }

        String stripPrefix(final String path) {
            final String stripPrefix = GenerateSourcesTask.this.stripPrefix();
            if (!stripPrefix.isEmpty() && path.startsWith(stripPrefix)) {
                final String result = path.substring(stripPrefix.length());
                if (result.isEmpty()) {
                    logger().warn("stripPrefix equals template directory - will not remove it from: " + path);
                } else {
                    return result;
                }
            }
            return path;
        }

        String stripSuffix(final String path) {
            final String stripSuffix = GenerateSourcesTask.this.stripSuffix();
            if (!stripSuffix.isEmpty() && path.endsWith(stripSuffix)) {
                final String result = path.substring(0, path.length() - stripSuffix.length());
                if (result.isEmpty() || result.endsWith("/")) {
                    logger().warn("stripSuffix equals file name - will not remove it from: " + path);
                } else {
                    return result;
                }
            }
            return path;
        }
    }
}

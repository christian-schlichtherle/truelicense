/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin;

import org.apache.maven.model.FileSet;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.ToolManager;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Processes template files in a given directory using
 * <a href="http://velocity.apache.org">Apache Velocity</a>.
 *
 * @since TrueLicense 2.4
 * @author Christian Schlichtherle
 */
public abstract class GenerateSourcesMojo extends MojoAdapter {

    private final JavaTool javaTool = new JavaTool();
    private final ScalaTool scalaTool = new ScalaTool();
    private final VersionTool versiontool = new VersionTool();

    /**
     * The character set to use for reading the template files and writing the
     * output files.
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding;

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    /**
     * A set of properties to put into the Velocity context so that they can
     * get referenced in template files.
     */
    @Parameter
    private Properties properties = new Properties();

    /**
     * The suffix to strip from the names of the output files.
     */
    @Parameter(property = "truelicense.generate.stripSuffix", defaultValue = ".vtl")
    private String stripSuffix;

    private final ToolManager manager = new ToolManager();
    private VelocityEngine engine;

    protected void doExecute() throws MojoFailureException {
        try {
            for (FileSet fileSet : fileSets())
                new TemplateSet(fileSet).processDirectory();
        } catch (Exception e) {
            throw new MojoFailureException(e.toString(), e);
        }
    }

    private List<FileSet> fileSets() {
        List<FileSet> templateSets = templateSets();
        if (null == templateSets) {
            templateSets = new LinkedList<FileSet>();
            final FileSet templateSet = new FileSet();
            templateSet.setDirectory(stripPrefix() + "java");
            templateSet.addInclude("**/*" + stripSuffix());
            templateSets.add(templateSet);
        }
        return templateSets;
    }

    VelocityEngine engine() {
        final VelocityEngine e = engine;
        return null != e ? e : (engine = engine0());
    }

    private VelocityEngine engine0() {
        final VelocityEngine e = new VelocityEngine();
        e.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new LogChuteAdapter(getLog()));
        e.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, baseDirectory().getPath());
        e.init();
        return e;
    }

    File baseDirectory() { return project.getBasedir(); }

    static String list2csv(final List<String> strings) {
        String separator = "";
        final StringBuilder b = new StringBuilder();
        for (final String item : strings) {
            b.append(separator).append(item);
            separator = ", ";
        }
        return b.toString();
    }

    static void mkdirs(File directory) throws IOException {
        if (!directory.isDirectory() && !directory.mkdirs())
            throw new IOException(directory + ": Failed to create directories.");
    }

    protected abstract GenerateSourcesStrategy generateSourcesStrategy();

    protected abstract List<FileSet> templateSets();

    protected abstract File outputDirectory();

    protected abstract String stripPrefix();

    protected String stripSuffix() { return stripSuffix; }

    final class TemplateSet {

        private final FileSet templates;

        private File mergeDirectory;

        TemplateSet(final FileSet templates) {
            this.templates = templates;
        }

        void processDirectory() throws Exception {
            final File templateDirectory = resolveWithBaseDirectory(templateDirectoryPath());
            final List<String> paths = FileUtils.getFileNames(templateDirectory,
                    list2csv(templates.getIncludes()),
                    list2csv(templates.getExcludes()),
                    false);
            if (paths.isEmpty()) {
                getLog().warn("Skipping empty " + templates + ".");
            } else {
                getLog().info("Template directory: " + templateDirectory);
                getLog().info("Merge directory: " + mergeDirectory());
                for (String path : paths) processFile(path);
                generateSourcesStrategy().updateProjectFrom(this);
            }
        }

        private void processFile(final String templatePath) throws Exception {
            final String expandedTemplatePath = expandWithTemplateDirectoryPath(templatePath);
            final String mergePath = stripSuffix(templatePath);
            final File mergeFile = resolveWithMergeDirectory(mergePath);
            if (getLog().isDebugEnabled()) {
                final File templateFile = resolveWithBaseDirectory(expandedTemplatePath);
                getLog().debug("Template file: " + templateFile);
                getLog().debug("Merge file: " + mergeFile);
            }
            mkdirs(mergeFile.getParentFile());
            try (OutputStream out = new FileOutputStream(mergeFile);
                 Writer writer = new OutputStreamWriter(out, encoding)) {
                engine().getTemplate(expandedTemplatePath, encoding)
                        .merge(context(), writer);
            }
        }

        private Context context() {
            final Context c = manager.createContext();
            c.put("java", javaTool);
            c.put("project", project);
            c.put("scala", scalaTool);
            c.put("version", versiontool);
            for (final Properties p : new Properties[] {
                    project.getProperties(),
                    properties,
            }) {
                for (String key : p.stringPropertyNames())
                    c.put(key, p.getProperty(key));
            }
            if (getLog().isDebugEnabled()) debugContext(c);
            return c;
        }

        private void debugContext(Context c) {
            String delimiter = "";
            final StringBuilder s = new StringBuilder();
            for (final Object o : c.getKeys()) {
                s.append(delimiter).append(o.toString());
                delimiter = ", ";
            }
            getLog().debug("Populated a new Velocity context with the following keys: " + s.toString());
        }

        private String expandWithTemplateDirectoryPath(String path) {
            return templateDirectoryPath() + '/' + path;
        }

        private File resolveWithMergeDirectory(String path) {
            return new File(mergeDirectory(), path);
        }

        private File resolveWithBaseDirectory(String path) {
            return new File(baseDirectory(), path);
        }

        void addMergeDirectoryToCompileSourceRoot() {
            project.addCompileSourceRoot(pathOfMergeDirectory());
        }

        void addMergeDirectoryToTestCompileSourceRoot() {
            project.addTestCompileSourceRoot(pathOfMergeDirectory());
        }

        private String pathOfMergeDirectory() {
            return mergeDirectory().getPath();
        }

        private File mergeDirectory() {
            final File d = mergeDirectory;
            return null != d ? d : (mergeDirectory = mergeDirectory0());
        }

        private File mergeDirectory0() {
            return resolveWithOutputDirectory(stripPrefix(templateDirectoryPath()));
        }

        private File resolveWithOutputDirectory(String path) {
            return new File(outputDirectory(), path);
        }

        private String templateDirectoryPath() {
            return templates.getDirectory();
        }

        private String stripPrefix(final String path) {
            final String stripPrefix = GenerateSourcesMojo.this.stripPrefix();
            if (!stripPrefix.isEmpty() && path.startsWith(stripPrefix)) {
                final String result = path.substring(stripPrefix.length());
                if (result.isEmpty()) {
                    getLog().warn(path + ": stripPrefix equals template directory - will not remove it.");
                } else {
                    return result;
                }
            }
            return path;
        }

        private String stripSuffix(final String path) {
            if (!stripSuffix.isEmpty() && path.endsWith(stripSuffix)) {
                final String result = path.substring(0, path.length() - stripSuffix.length());
                if (result.isEmpty() || result.endsWith("/")) {
                    getLog().warn(path + ": stripSuffix equals file name - will not remove it.");
                } else {
                    return result;
                }
            }
            return path;
        }
    } // TemplateSet
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TVFS;
import net.java.truelicense.maven.plugin.obfuscation.Processor;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;

import static java.io.File.createTempFile;

/**
 * A basic MOJO for the obfuscation of constant string values in Java class
 * files (byte code).
 *
 * @see net.java.truelicense.obfuscate.ObfuscatedString
 * @since TrueLicense 2.4
 * @author Christian Schlichtherle
 */
public abstract class ObfuscateClassesMojo extends MojoAdapter {

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

    /**
     * Whether or not the class files shall get backed up to a JAR file before
     * processing them.
     *
     * @deprecated This (non-)feature will be removed in TrueLicense 3.0.
     */
    @Deprecated
    @Parameter(property = "truelicense.obfuscate.backup", defaultValue = "false")
    private boolean backup;

    /**
     * The prefix of the backup JAR file with the original class files.
     *
     * @deprecated This (non-)feature will be removed in TrueLicense 3.0.
     */
    @Deprecated
    @Parameter(property = "truelicense.obfuscate.backupPrefix", defaultValue = "classes")
    private String backupPrefix;

    /**
     * The extension of the backup directory with the original class files:
     * Use <code>"zip"</code> to create a ZIP file as the backup directory.
     *
     * @deprecated This (non-)feature will be removed in TrueLicense 3.0.
     */
    @Deprecated
    @Parameter(property = "truelicense.obfuscate.backupExtension", defaultValue = "zip")
    private String backupExtension;

    /** The maximum allowed size of a class file in bytes. */
    @Parameter(property = "truelicense.obfuscate.maxBytes", defaultValue = "" + Processor.DEFAULT_MAX_BYTES)
    private int maxBytes;

    /**
     * The scope of constant string value obfuscation:
     * <ul>
     * <li>Use <code>none</code> to skip obfuscation.
     * <li>Use <code>annotated</code> to obfuscate only constant string
     *     values of fields which have been annotated with @Obfuscate.
     * <li>Use <code>all</code> to obfuscate all constant string values.
     *     This may result in quite some code size and runtime overhead,
     *     so you should <em>not</em> use this in general.
     * </ul>
     */
    @Parameter(property = "truelicense.obfuscate.scope", defaultValue = "annotated")
    private Scope scope;

    /**
     * The format for synthesized method names.
     * This a format string for the class {@link Formatter}.
     * It's first parameter is a string identifier for the obfuscation stage
     * and its second parameter is an integer index for the synthesized method.
     */
    @Parameter(property = "truelicense.obfuscate.methodNameFormat", defaultValue = Processor.DEFAULT_METHOD_NAME_FORMAT)
    private String methodNameFormat;

    /**
     * Whether or not a call to <code>java.lang.String.intern()</code>
     * shall get added when computing the original constant string values again.
     * Use this to preserve the identity relation of constant string values if
     * required.
     */
    @Parameter(property = "truelicense.obfuscate.intern", defaultValue = "" + Processor.DEFAULT_INTERN_STRINGS)
    private boolean intern;

    protected abstract TFile outputDirectory();

    @Override
    protected final void doExecute() throws MojoFailureException {
        try {
            if (scope == Scope.none) {
                getLog().warn("Skipping constant string value obfuscation.");
            } else {
                getLog().info(String.format(
                        "Obfuscating %s constant string values in %s.",
                        scope,
                        outputDirectory()));
                try {
                    backup();
                    obfuscate();
                } finally {
                    TVFS.umount();
                }
            }
        } catch (Exception e) {
            throw new MojoFailureException(e.toString(), e);
        }
    }

    @Deprecated
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void backup() throws IOException {
        if (!backup) return;
        final TFile backupDir = new TFile(createTempFile(
                backupPrefix + '.',
                '.' + backupExtension,
                buildDirectory));
        getLog().info(backupDir + ": Creating class file backup directory.");
        outputDirectory().cp_rp(backupDir.rm_r());
    }

    private void obfuscate() {
        Processor.builder()
                .logger(new LoggerAdapter(getLog()))
                .directory(outputDirectory())
                .maxBytes(maxBytes)
                .obfuscateAll(scope == Scope.all)
                .methodNameFormat(methodNameFormat)
                .internStrings(intern)
                .build()
                .run();
    }
}

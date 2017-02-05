/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.maven.plugin.obfuscation;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.truelicense.maven.plugin.commons.MojoAdapter;
import org.truelicense.maven.plugin.obfuscation.core.Processor;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;

/**
 * A basic MOJO for the obfuscation of constant string values in Java class
 * files (byte code).
 *
 * @see org.truelicense.obfuscate.ObfuscatedString
 * @since TrueLicense 2.4
 * @author Christian Schlichtherle
 */
public abstract class ObfuscateClassesMojo extends MojoAdapter {

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

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

    protected abstract File outputDirectory();

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
                obfuscate();
            }
        } catch (IOException e) {
            throw new MojoFailureException(e.toString(), e);
        }
    }

    private void obfuscate() throws IOException {
        Processor.builder()
                .logger(new LoggerAdapter(getLog()))
                .directory(outputDirectory().toPath())
                .maxBytes(maxBytes)
                .obfuscateAll(scope == Scope.all)
                .methodNameFormat(methodNameFormat)
                .internStrings(intern)
                .build()
                .execute();
    }
}

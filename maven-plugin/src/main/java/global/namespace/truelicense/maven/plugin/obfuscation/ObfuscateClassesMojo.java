/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.obfuscation;

import global.namespace.truelicense.build.tasks.commons.Task;
import global.namespace.truelicense.build.tasks.obfuscation.ObfuscateClassesTask;
import global.namespace.truelicense.build.tasks.obfuscation.Scope;
import global.namespace.truelicense.maven.plugin.commons.BasicMojo;
import global.namespace.truelicense.obfuscate.ObfuscatedString;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Formatter;

import static global.namespace.neuron.di.java.Incubator.wire;

/**
 * A basic MOJO for the obfuscation of constant string values in Java class
 * files (byte code).
 *
 * @see ObfuscatedString
 */
public abstract class ObfuscateClassesMojo extends BasicMojo {

    /**
     * Whether or not a call to <code>java.lang.String.intern()</code>
     * shall get added when computing the original constant string values again.
     * Use this to preserve the identity relation of constant string values if
     * required.
     */
    @Parameter(property = "truelicense.obfuscate.intern", defaultValue = "" + ObfuscateClassesTask.INTERN)
    private boolean intern;

    /** The maximum allowed size of a class file in bytes. */
    @Parameter(property = "truelicense.obfuscate.maxBytes", defaultValue = "" + ObfuscateClassesTask.MAX_BYTES)
    private int maxBytes;

    /**
     * The format for synthesized method names.
     * This a format string for the class {@link Formatter}.
     * It's first parameter is a string identifier for the obfuscation stage
     * and its second parameter is an integer index for the synthesized method.
     */
    @Parameter(property = "truelicense.obfuscate.methodNameFormat", defaultValue = ObfuscateClassesTask.METHOD_NAME_FORMAT)
    private String methodNameFormat;

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

    @Override
    protected final Task task() {
        return wire(ObfuscateClassesTask.class).using(this);
    }
}

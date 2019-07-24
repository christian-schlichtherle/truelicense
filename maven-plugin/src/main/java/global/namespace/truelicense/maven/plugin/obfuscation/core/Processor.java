/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.maven.plugin.obfuscation.core;

import org.objectweb.asm.ClassVisitor;
import org.slf4j.Logger;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Runs the obfuscation based on the configuration parameters provided to its
 * constructor(s).
 *
 * @author Christian Schlichtherle
 */
public final class Processor {

    /**
     * The default value of the {@code maxBytes} property, which is {@value}.
     *
     * @see #maxBytes()
     */
    public static final int DEFAULT_MAX_BYTES = 64 * 1024;

    /**
     * The default value of the {@code obfuscateAll} property, which is {@value}.
     *
     * @see #obfuscateAll()
     */
    public static final boolean DEFAULT_OBFUSCATE_ALL = false;

    /**
     * The default value of the {@code methodNameFormat} property, which is {@value}.
     *
     * @see #methodNameFormat()
     */
    public static final String DEFAULT_METHOD_NAME_FORMAT = "_%s#%d";

    /**
     * The default value of the {@code internStrings} property, which is {@value}.
     *
     * @see #internStrings()
     */
    public static final boolean DEFAULT_INTERN_STRINGS = true;

    private final Set<String> constantStrings = new HashSet<>();

    private final Logger logger;
    private final Path directory;
    private final int maxBytes;
    private final boolean obfuscateAll;
    private final String methodNameFormat;
    private final boolean internStrings;

    /** Returns a new builder for a processor. */
    public static Builder builder() { return new Builder(); }

    Processor(final Builder b) {
        this.logger = requireNonNull(b.logger);
        this.directory = requireNonNull(b.directory);
        if (0 >= (this.maxBytes = nonNullOr(b.maxBytes, DEFAULT_MAX_BYTES)))
            throw new IllegalArgumentException();
        this.obfuscateAll = nonNullOr(b.obfuscateAll, DEFAULT_OBFUSCATE_ALL);
        this.methodNameFormat = nonNullOr(b.methodNameFormat, DEFAULT_METHOD_NAME_FORMAT);
        this.internStrings = nonNullOr(b.internStrings, DEFAULT_INTERN_STRINGS);
    }

    private static <T> T nonNullOr(T value, T def) {
        return null != value ? value : def;
    }

    /**
     * Returns the logger to use.
     * Any errors should be logged at the error level to enable subsequent
     * checking.
     */
    public Logger logger() { return logger; }

    /** Returns the directory to scan for class files to process. */
    public Path directory() { return directory; }

    /** Returns the maximum allowed size of a class file in bytes. */
    public int maxBytes() { return maxBytes;  }

    /**
     * Returns {@code true} if all constant string values shall get obfuscated.
     * Otherwise only constant string values of fields annotated with
     * {@link Obfuscate} shall get obfuscated.
     *
     * @return Whether or not all constant string values shall get obfuscated.
     */
    public boolean obfuscateAll() { return obfuscateAll; }

    /**
     * Returns the methodName for synthesized method names.
     * This a methodName string for the class {@link Formatter}.
     * It's first parameter is a string identifier for the obfuscation stage
     * and its second parameter is an integer index for the synthesized method.
     */
    public String methodNameFormat() { return methodNameFormat; }

    /**
     * Returns whether or not a call to <code>java.lang.String.intern()</code>
     * shall get added when computing the original constant string values again.
     * Use this to preserve the identity relation of constant string values if
     * required.
     */
    public boolean internStrings() { return internStrings; }

    /**
     * Returns the set of constant strings to obfuscate.
     * The returned set is only used when {@link #obfuscateAll} is
     * {@code false} and is modifiable so as to exchange the set between
     * different processing paths.
     *
     * @return The set of constant strings to obfuscate.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    Set<String> constantStrings() { return constantStrings; }

    Logger logger(String subject) {
        return new SubjectLogger(logger(), subject);
    }

    String methodName(String stage, int index) {
        return new Formatter().format(methodNameFormat(), stage, index).toString();
    }

    /** Executes this obfuscation processor. */
    public void execute() throws IOException {
        firstPass().execute();
        secondPass().execute();
    }

    Pass firstPass() { return new FirstPass(this); }
    Pass secondPass() { return new SecondPass(this); }

    ClassVisitor collector() { return new Collector(this); }

    ClassVisitor obfuscator(ClassVisitor cv) {
        return new Obfuscator(this, cv);
    }

    ClassVisitor merger(ClassVisitor cv, String prefix) {
        return new Merger(this, prefix, cv);
    }

    public static final class Builder {

        Logger logger;
        Path directory;
        Integer maxBytes;
        Boolean obfuscateAll;
        String methodNameFormat;
        Boolean internStrings;

        Builder() { }

        public Builder logger(final Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder directory(final Path directory) {
            this.directory = directory;
            return this;
        }

        public Builder maxBytes(final Integer maxBytes) {
            this.maxBytes = maxBytes;
            return this;
        }

        public Builder obfuscateAll(final Boolean obfuscateAll) {
            this.obfuscateAll = obfuscateAll;
            return this;
        }

        public Builder methodNameFormat(final String methodNameFormat) {
            this.methodNameFormat = methodNameFormat;
            return this;
        }

        public Builder internStrings(final Boolean internStrings) {
            this.internStrings = internStrings;
            return this;
        }

        public Processor build() { return new Processor(this); }
    }
}

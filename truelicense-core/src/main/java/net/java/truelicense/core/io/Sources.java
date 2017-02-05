/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.CheckForNull;

/**
 * Provides common {@link Source}s.
 *
 * @author Christian Schlichtherle
 */
public class Sources {

    /**
     * Returns a source which reads from standard input without ever closing it.
     */
    public static Source input() { return uncloseable(System.in); }

    /**
     * Returns a source which reads from the given input stream and ignores any
     * call to the {@link InputStream#close} method of the input stream.
     *
     * @param in the input stream to use.
     */
    public static Source uncloseable(final InputStream in) {
        return new Source() {
            @Override public InputStream input() {
                return new FilterInputStream(in) {
                    @Override public void close() throws IOException { }
                };
            }
        };
    }

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * This method will use the given class to resolve the resource name and
     * the class loader as described in
     * {@link Class#getResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  clazz the class to use for loading the resource.
     * @return A source which loads the resource with the given {@code name}.
     */
    public static Source forResource(final String name, final Class<?> clazz) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return check(clazz.getResourceAsStream(name), name);
            }
        };
    }

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * If the given class loader is not {@code null}, then the resource will
     * get loaded as described in
     * {@link ClassLoader#getResourceAsStream(String)}.
     * Otherwise, the resource will get loaded as described in
     * {@link ClassLoader#getSystemResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  loader the nullable class loader to use for loading the resource.
     *         If this is {@code null}, then the system class loader will get
     *         used.
     * @return A source which loads the resource with the given {@code name}.
     */
    public static Source forResource(
            final String name,
            final @CheckForNull ClassLoader loader) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return check(null != loader
                        ? loader.getResourceAsStream(name)
                        : ClassLoader.getSystemResourceAsStream(name), name);
            }
        };
    }

    static InputStream check(final @CheckForNull InputStream in, final String name)
    throws FileNotFoundException {
        if (null == in) throw new FileNotFoundException(name);
        return in;
    }

    private Sources() { }
}

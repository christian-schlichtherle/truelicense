/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides common {@link Sink}s.
 *
 * @author Christian Schlichtherle
 */
public class Sinks {

    /**
     * Returns a sink which writes to standard output without ever closing it.
     */
    public static Sink output() { return uncloseable(System.out); }

    /**
     * Returns a sink which writes to standard error without ever closing it.
     */
    public static Sink error() { return uncloseable(System.err); }

    /**
     * Returns a sink which writes to the given output stream and just
     * {@linkplain OutputStream#flush flushes} the output stream instead of
     * {@linkplain OutputStream#close closing} it.
     *
     * @param out the output stream to use.
     */
    public static Sink uncloseable(final OutputStream out) {
        return new Sink() {
            @Override public OutputStream output() {
                return new FilterOutputStream(out) {
                    @Override public void close() throws IOException {
                        out.flush();
                    }
                };
            }
        };
    }

    private Sinks() { }
}

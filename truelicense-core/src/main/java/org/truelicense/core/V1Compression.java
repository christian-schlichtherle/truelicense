/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import java.io.*;
import java.util.zip.*;
import javax.annotation.concurrent.Immutable;
import org.truelicense.core.io.*;

/**
 * The compression for V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class V1Compression implements Transformation {

    @Override public Sink apply(final Sink sink) {
        return new Sink() {
            @Override public OutputStream output() throws IOException {
                final OutputStream out = sink.output();
                try {
                    return new GZIPOutputStream(out, Store.BUFSIZE);
                } catch (final IOException ex) { // TODO: make this a Throwable for Java 7
                    try { out.close(); }
                    finally { throw ex; }
                }
            }
        };
    }

    @Override public Source unapply(final Source source) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                final InputStream in = source.input();
                try {
                    return new GZIPInputStream(in, Store.BUFSIZE);
                } catch (final IOException ex) { // TODO: make this a Throwable for Java 7
                    try { in.close(); }
                    finally { throw ex; }
                }
            }
        };
    }
}

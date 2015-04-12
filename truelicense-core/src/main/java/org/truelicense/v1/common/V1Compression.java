/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1.common;

import java.io.*;
import java.util.zip.*;
import javax.annotation.concurrent.Immutable;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;

/**
 * The compression for V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class V1Compression implements Transformation {

    @Override public Sink apply(final Sink sink) {
        return new Sink() {
            @Override public OutputStream output() throws IOException {
                final OutputStream out = sink.output();
                try {
                    return new GZIPOutputStream(out, Store.BUFSIZE);
                } catch (final Throwable t) {
                    try { out.close(); }
                    catch (Throwable t2) { t.addSuppressed(t2); }
                    throw t;
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
                } catch (final Throwable t) {
                    try { in.close(); }
                    catch (Throwable t2) { t.addSuppressed(t2); }
                    throw t;
                }
            }
        };
    }
}

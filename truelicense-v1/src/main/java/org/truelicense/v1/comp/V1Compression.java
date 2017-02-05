/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1.comp;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The compression for V1 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V1Compression implements Transformation {

    @Override public Sink apply(final Sink sink) {
        return () -> {
            final OutputStream out = sink.output();
            try {
                return new GZIPOutputStream(out, Store.BUFSIZE);
            } catch (final Throwable t) {
                try { out.close(); }
                catch (Throwable t2) { t.addSuppressed(t2); }
                throw t;
            }
        };
    }

    @Override public Source unapply(final Source source) {
        return () -> {
            final InputStream in = source.input();
            try {
                return new GZIPInputStream(in, Store.BUFSIZE);
            } catch (final Throwable t) {
                try { in.close(); }
                catch (Throwable t2) { t.addSuppressed(t2); }
                throw t;
            }
        };
    }
}

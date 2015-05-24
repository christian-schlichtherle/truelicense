/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import java.io.*;
import java.util.zip.*;
import javax.annotation.concurrent.Immutable;
import net.java.truelicense.core.io.*;

/**
 * The compression for V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class V2Compression implements Transformation {

    @Override public Sink apply(final Sink sink) {
        return new Sink() {
            @Override public OutputStream output() throws IOException {
                return new DeflaterOutputStream(sink.output(),
                        new Deflater(Deflater.BEST_COMPRESSION),
                        Store.BUFSIZE) {

                    boolean closed;

                    @Override
                    public void close() throws IOException {
                        if (!closed) {
                            closed = true;
                            try {
                                try { finish(); }
                                finally { def.end(); }
                            } finally {
                                super.close();
                            }
                        }
                    }
                };
            }
        };
    }

    @Override public Source unapply(final Source source) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return new InflaterInputStream(source.input(),
                        new Inflater(),
                        Store.BUFSIZE) {

                    boolean closed;

                    @Override
                    public void close() throws IOException {
                        if (!closed) {
                            closed = true;
                            try { inf.end(); }
                            finally { super.close(); }
                        }
                    }
                };
            }
        };
    }
}

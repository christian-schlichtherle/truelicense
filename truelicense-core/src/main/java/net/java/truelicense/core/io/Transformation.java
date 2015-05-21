/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.io;

/**
 * A transformation decorates the {@link java.io.OutputStream}s and
 * {@link java.io.InputStream}s which are provided by {@linkplain Sink sinks}
 * and {@linkplain Source sources}.
 * <p>
 * For example, in an encryption transformation, the method {@link #apply}
 * could decorate the output streams provided by the given sink with a new
 * {@link javax.crypto.CipherOutputStream} in order to encrypt the data before
 * writing it to the underlying output stream.
 * Likewise, the method {@link #unapply} could decorate the input streams
 * provided by the given source with a new
 * {@link javax.crypto.CipherInputStream} in order to decrypt the data after
 * reading it from the underlying input stream.
 * <p>
 * As another example, in a compression transformation, the apply method could
 * decorate the output streams provided by the given sink with a new
 * {@link java.util.zip.DeflaterOutputStream} in order to compress the data
 * before writing it to the underlying output stream.
 * Likewise, the unapply method could decorate the input streams provided by the
 * given source with a new {@link java.util.zip.InflaterInputStream} in order to
 * decompress the data after reading it from the underlying input stream.
 *
 * @author Christian Schlichtherle
 */
public interface Transformation {

    /**
     * Returns a sink which decorates the output streams provided by the given
     * sink.
     *
     * @param  sink the sink which provides the output streams to decorate.
     */
    Sink apply(Sink sink);

    /**
     * Returns a source which decorates the input streams provided by the given
     * source.
     *
     * @param  source the source which provides the input streams to decorate.
     */
    Source unapply(Source source);
}

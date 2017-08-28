/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.io;

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
 * <p>
 * The benefit of this interface is that you can easily chain the apply and
 * unapply methods in order to create rich decorators for the underlying sinks
 * and sources without needing to know anything about the implementation of the
 * transformations.
 * <p>
 * For example, depending on the previous examples, the following test code
 * would assert the round-trip processing of the string {@code "Hello world!"}
 * using the composition of some compression and encryption transformations on
 * some store:
 * <pre>{@code
 * Transformation compression = ...;
 * Transformation encryption = ...;
 * Store store = ...;
 * Sink compressAndEncryptData = compression.apply(encryption.apply(store));
 * Source decryptAndDecompressData = compression.unapply(encryption.unapply(store));
 * try (PrintWriter writer = new PrintWriter(compressAndEncryptData.output())) {
 *     writer.println("Hello world!");
 * }
 * try (BufferedReader reader = new BufferedReader(new InputStreamReader(decryptAndDecompressData.input()))) {
 *     assertTrue("Hello world!".equals(reader.readLine()));
 * }
 * }</pre>
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

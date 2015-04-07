/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.codec;

import java.io.*;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.truelicense.api.codec.Codec;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.obfuscate.Obfuscate;

/**
 * A codec which encodes/decodes an object with an
 * {@link ObjectOutputStream}/{@link ObjectInputStream}.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class SerializationCodec implements Codec {

    @Obfuscate
    private static final String CONTENT_TYPE = "application/x-java-serialized-object";

    @Obfuscate
    private static final String CONTENT_TRANSFER_ENCODING = "binary";

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link SerializationCodec}
     * returns {@code "application/x-java-serialized-object"}.
     *
     * @see java.awt.datatransfer.DataFlavor
     */
    @Override public String contentType() { return CONTENT_TYPE; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link SerializationCodec}
     * returns {@code "binary"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>
     */
    @Override public String contentTransferEncoding() {
        return CONTENT_TRANSFER_ENCODING;
    }

    @Override
    public void encode(final Sink sink, final @Nullable Object obj) throws Exception {
        try (OutputStream out = sink.output();
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(obj);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T decode(final Source source, final Type expected)
    throws Exception {
        try (InputStream in = source.input();
             ObjectInputStream oin = new ObjectInputStream(in)) {
            return (T) oin.readObject();
        }
    }
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.it.core;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import net.truelicense.api.codec.Codec;
import net.truelicense.obfuscate.Obfuscate;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * A codec which encodes/decodes an object with an
 * {@link ObjectOutputStream}/{@link ObjectInputStream}.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
class SerializationCodec implements Codec {

    @Obfuscate
    private static final String CONTENT_TYPE = "application/x-java-serialized-object";

    @Obfuscate
    private static final String CONTENT_TRANSFER_ENCODING = "binary";

    private static final global.namespace.fun.io.api.Codec
            CODEC = global.namespace.fun.io.bios.BIOS.serializationCodec();

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link SerializationCodec}
     * returns {@code "application/x-java-serialized-object"}.
     *
     * @see java.awt.datatransfer.DataFlavor
     */
    @Override
    public String contentType() { return CONTENT_TYPE; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link SerializationCodec}
     * returns {@code "binary"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>
     */
    @Override
    public String contentTransferEncoding() { return CONTENT_TRANSFER_ENCODING; }

    @Override
    public Encoder encoder(Socket<OutputStream> output) { return CODEC.encoder(output); }

    @Override
    public Decoder decoder(Socket<InputStream> input) { return CODEC.decoder(input); }
}

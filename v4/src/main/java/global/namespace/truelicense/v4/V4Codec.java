/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v4;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.io.InputStream;
import java.io.OutputStream;

import static global.namespace.fun.io.jackson.Jackson.json;

/**
 * A codec for use with V4 format license keys.
 */
final class V4Codec implements Codec {

    @Obfuscate
    private static final String CONTENT_TYPE = "application/json";

    @Obfuscate
    private static final String CONTENT_TRANSFER_ENCODING = "8bit";

    private final global.namespace.fun.io.api.Codec codec;

    V4Codec(V4CodecFactory factory) {
        this.codec = json(factory::objectMapper);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V4Codec}
     * returns {@code "application/json"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4627">RFC 4627</a>
     */
    @Override
    public String contentType() {
        return CONTENT_TYPE;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V4Codec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4627">RFC 4627</a>
     */
    @Override
    public String contentTransferEncoding() {
        return CONTENT_TRANSFER_ENCODING;
    }

    @Override
    public Encoder encoder(Socket<OutputStream> output) {
        return codec.encoder(output);
    }

    @Override
    public Decoder decoder(Socket<InputStream> input) {
        return codec.decoder(input);
    }
}

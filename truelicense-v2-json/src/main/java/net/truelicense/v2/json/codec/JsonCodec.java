/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.json.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import net.truelicense.api.codec.Codec;
import net.truelicense.obfuscate.Obfuscate;

import java.io.InputStream;
import java.io.OutputStream;

import static global.namespace.fun.io.jackson.Jackson.jsonCodec;

/**
 * A codec which encodes/decodes objects to/from JSON with an
 * {@link ObjectMapper}.
 * This type of codec is used for V2/JSON format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class JsonCodec implements Codec {

    @Obfuscate
    private static final String CONTENT_TYPE = "application/json";

    @Obfuscate
    private static final String CONTENT_TRANSFER_ENCODING = "8bit";

    private final global.namespace.fun.io.api.Codec codec;

    /**
     * Constructs a new JSON codec.
     *
     * @param mapper the object mapper.
     */
    public JsonCodec(final ObjectMapper mapper) { this.codec = jsonCodec(mapper); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JsonCodec}
     * returns {@code "application/json"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4627">RFC 4627</a>
     */
    @Override
    public String contentType() { return CONTENT_TYPE; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JsonCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4627">RFC 4627</a>
     */
    @Override
    public String contentTransferEncoding() { return CONTENT_TRANSFER_ENCODING; }

    @Override
    public Encoder encoder(Socket<OutputStream> output) { return codec.encoder(output); }

    @Override
    public Decoder decoder(Socket<InputStream> input) { return codec.decoder(input); }
}

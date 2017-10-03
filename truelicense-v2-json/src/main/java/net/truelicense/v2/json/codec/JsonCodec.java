/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.json.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.truelicense.api.codec.Codec;
import net.truelicense.api.codec.Decoder;
import net.truelicense.api.codec.Encoder;
import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.obfuscate.Obfuscate;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Objects;

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

    private final ObjectMapper mapper;

    /**
     * Constructs a new JSON codec.
     *
     * @param mapper the object mapper.
     */
    public JsonCodec(final ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JsonCodec}
     * returns {@code "application/json"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4627">RFC 4627</a>
     */
    @Override public String contentType() { return CONTENT_TYPE; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JsonCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc4627">RFC 4627</a>
     */
    @Override public String contentTransferEncoding() {
        return CONTENT_TRANSFER_ENCODING;
    }

    @Override public Encoder encoder(final Sink sink) {
        return obj -> {
            try (OutputStream out = sink.output()) {
                mapper.writeValue(out, obj);
            }
        };
    }

    @Override public Decoder decoder(final Source source) {
        return new Decoder() {
            @Override
            public <T> T decode(final Type expected) throws Exception {
                try (InputStream in = source.input()) {
                    return mapper.readValue(in, mapper.constructType(expected));
                }
            }
        };
    }
}

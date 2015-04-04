/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.json.codec;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import net.java.truelicense.core.codec.Codec;
import net.java.truelicense.core.io.*;
import net.java.truelicense.core.util.Objects;
import net.java.truelicense.obfuscate.Obfuscate;

/**
 * A codec which encodes/decodes objects to/from JSON with an
 * {@link ObjectMapper}.
 *
 * @author Christian Schlichtherle
 */
@Immutable
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

    @Override
    public void encode(final Sink sink, final @Nullable Object obj)
    throws Exception {
        final OutputStream out = sink.output();
        try { mapper.writeValue(out, obj); }
        finally { out.close(); }
    }

    @Override
    public @Nullable <T> T decode(final Source source, final Type expected)
    throws Exception {
        final InputStream in = source.input();
        try { return mapper.readValue(in, mapper.constructType(expected)); }
        finally { in.close(); }
    }
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v1;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.io.InputStream;
import java.io.OutputStream;

import static global.namespace.fun.io.bios.BIOS.xml;

/**
 * A codec for use with V1 format license keys.
 */
final class V1Codec implements Codec {

    @Obfuscate
    private static final String APPLICATION_XML_WITH_UTF_8 = "application/xml; charset=utf-8";

    @Obfuscate
    private static final String EIGHT_BIT = "8bit";

    private final global.namespace.fun.io.api.Codec codec;

    V1Codec(V1CodecFactory factory) {
        codec = xml(factory::xmlEncoder, factory::xmlDecoder);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1Codec}
     * returns {@code "application/xml; charset=utf-8"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override
    public String contentType() { return APPLICATION_XML_WITH_UTF_8; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link V1Codec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override
    public String contentTransferEncoding() { return EIGHT_BIT; }

    @Override
    public Encoder encoder(Socket<OutputStream> output) { return codec.encoder(output); }

    @Override
    public Decoder decoder(Socket<InputStream> input) { return codec.decoder(input); }
}

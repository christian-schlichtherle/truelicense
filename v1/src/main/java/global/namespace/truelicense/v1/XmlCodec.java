/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v1;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;

import static global.namespace.fun.io.bios.BIOS.xml;

/**
 * A codec which encodes/decodes objects to/from XML with an
 * {@link XMLEncoder}/{@link XMLDecoder}.
 * This type of codec is used for V1 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
class XmlCodec implements Codec {

    @Obfuscate
    private static final String APPLICATION_XML_WITH_UTF_8 = "application/xml; charset=utf-8";

    @Obfuscate
    private static final String EIGHT_BIT = "8bit";

    private final global.namespace.fun.io.api.Codec codec = xml(this::encoder, this::decoder);

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link XmlCodec}
     * returns {@code "application/xml; charset=utf-8"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override
    public String contentType() { return APPLICATION_XML_WITH_UTF_8; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link XmlCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override
    public String contentTransferEncoding() { return EIGHT_BIT; }

    @Override
    public Encoder encoder(Socket<OutputStream> output) { return codec.encoder(output); }

    XMLEncoder encoder(OutputStream out) { return new XMLEncoder(out); }

    @Override
    public Decoder decoder(Socket<InputStream> input) { return codec.decoder(input); }

    XMLDecoder decoder(InputStream in) { return new XMLDecoder(in); }
}

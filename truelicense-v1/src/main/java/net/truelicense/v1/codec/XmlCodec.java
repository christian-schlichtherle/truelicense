/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v1.codec;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.bios.BIOS;
import net.truelicense.api.codec.Codec;
import net.truelicense.obfuscate.Obfuscate;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * A codec which encodes/decodes objects to/from XML with an
 * {@link XMLEncoder}/{@link XMLDecoder}.
 * This type of codec is used for V1 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class XmlCodec implements Codec {

    @Obfuscate
    private static final String APPLICATION_XML_WITH_UTF_8 = "application/xml; charset=utf-8";

    @Obfuscate
    private static final String EIGHT_BIT = "8bit";

    private final global.namespace.fun.io.api.Codec codec = BIOS.xmlCodec(this::encoder, this::decoder);

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

    /** Returns a new XML encoder. */
    protected XMLEncoder encoder(OutputStream out) { return new XMLEncoder(out); }

    @Override
    public Decoder decoder(Socket<InputStream> input) { return codec.decoder(input); }

    /** Returns a new XML decoder. */
    protected XMLDecoder decoder(InputStream in) { return new XMLDecoder(in); }
}

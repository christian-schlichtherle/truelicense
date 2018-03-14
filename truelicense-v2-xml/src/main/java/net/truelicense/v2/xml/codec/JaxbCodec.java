/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.xml.codec;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Encoder;
import global.namespace.fun.io.api.Socket;
import net.truelicense.api.codec.Codec;
import net.truelicense.obfuscate.Obfuscate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;

import static global.namespace.fun.io.jaxb.JAXB.xmlCodec;

/**
 * A codec which encodes/decodes objects to/from XML with a
 * {@link Marshaller}/{@link Unmarshaller} derived from a {@link JAXBContext}.
 * This type of codec is used for V1/XML format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class JaxbCodec implements Codec {

    @Obfuscate
    private static final String APPLICATION_XML_WITH_UTF_8 = "application/xml; charset=utf-8";

    @Obfuscate
    private static final String EIGHT_BIT = "8bit";

    /** The JAXB context provided to the constructor. */
    private final global.namespace.fun.io.api.Codec codec;

    public JaxbCodec(final JAXBContext context) { this.codec = xmlCodec(context); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JaxbCodec}
     * returns {@code "application/xml; charset=utf-8"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override
    public String contentType() { return APPLICATION_XML_WITH_UTF_8; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JaxbCodec}
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

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.codec;

import org.truelicense.api.codec.Codec;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A codec which encodes/decodes objects to/from XML with a
 * {@link Marshaller}/{@link Unmarshaller} derived from a {@link JAXBContext}.
 * <p>
 * This type of codec is used for V1/XML format license keys.
 * This type of codec does <em>not</em> support encoding or decoding
 * {@code null}.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class JaxbCodec implements Codec {

    /** The JAXB context provided to the constructor. */
    protected final JAXBContext context;

    public JaxbCodec(final JAXBContext context) {
        this.context = Objects.requireNonNull(context);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JaxbCodec}
     * returns {@code "application/xml; charset=utf-8"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentType() {
        return Codecs.APPLICATION_XML_WITH_UTF_8;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JaxbCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentTransferEncoding() {
        return Codecs._8BIT;
    }

    @Override public void encode(final Sink sink, final Object obj)
    throws Exception {
        try (OutputStream out = sink.output()) {
            marshaller().marshal(obj, out);
        }
    }

    /** Returns a new marshaller. */
    protected Marshaller marshaller() throws JAXBException {
        return context.createMarshaller();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(final Source source, final Type expected)
    throws Exception {
        try (InputStream in = source.input()) {
            return (T) unmarshaller().unmarshal(in);
        }
    }

    /** Returns a new unmarshaller. */
    protected Unmarshaller unmarshaller() throws JAXBException {
        return context.createUnmarshaller();
    }
}

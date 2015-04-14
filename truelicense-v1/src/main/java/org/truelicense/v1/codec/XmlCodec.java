/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1.codec;

import java.beans.*;
import java.io.*;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import javax.annotation.concurrent.*;

import org.truelicense.api.codec.Codec;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.obfuscate.Obfuscate;

/**
 * A codec which encodes/decodes objects to/from XML with an
 * {@link XMLEncoder}/{@link XMLDecoder}.
 * This type of codec is used for V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class XmlCodec implements Codec {

    @Obfuscate
    private static final String APPLICATION_XML_WITH_UTF_8 = "application/xml; charset=utf-8";

    @Obfuscate
    private static final String EIGHT_BIT = "8bit";

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link XmlCodec}
     * returns {@code "application/xml; charset=utf-8"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentType() {
        return APPLICATION_XML_WITH_UTF_8;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link XmlCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentTransferEncoding() {
        return EIGHT_BIT;
    }

    @Override
    public void encode(final Sink sink, final Object obj) throws Exception {
        final ZeroToleranceListener ztl = new ZeroToleranceListener();
        try (XMLEncoder enc = encoder(sink.output())) {
            enc.setExceptionListener(ztl);
            enc.writeObject(obj);
        }
        ztl.check();
    }

    /** Returns a new XML encoder. */
    protected XMLEncoder encoder(OutputStream out) {
        return new XMLEncoder(out);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(final Source source, final Type expected) throws Exception {
        final Object obj;
        final ZeroToleranceListener ztl = new ZeroToleranceListener();
        try (XMLDecoder dec = decoder(source.input())) {
            dec.setExceptionListener(ztl);
            obj = dec.readObject();
        }
        ztl.check();
        return (T) obj;
    }

    /** Returns a new XML decoder. */
    protected XMLDecoder decoder(InputStream in) {
        return new XMLDecoder(in);
    }

    @NotThreadSafe
    private static class ZeroToleranceListener implements ExceptionListener {

        Exception ex;

        @Override
        public void exceptionThrown(final Exception ex) {
            if (null == this.ex) this.ex = ex; // don't overwrite prior exception
        }

        void check() throws Exception { if (null != ex) throw ex; }
    } // ZeroToleranceListener
}

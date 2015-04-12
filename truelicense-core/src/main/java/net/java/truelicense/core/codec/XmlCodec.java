/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.codec;

import java.beans.*;
import java.io.*;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import javax.annotation.concurrent.*;
import net.java.truelicense.core.io.*;

/**
 * A codec which encodes/decodes objects to/from XML with an
 * {@link XMLEncoder}/{@link XMLDecoder}.
 * <p>
 * This type of codec is used for V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class XmlCodec implements Codec {

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link XmlCodec}
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
     * The implementation in the class {@link XmlCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentTransferEncoding() {
        return Codecs.EIGHT_BIT;
    }

    @Override public void encode(final Sink sink, final @Nullable Object obj)
    throws Exception {
        final ZeroToleranceListener ztl = new ZeroToleranceListener();
        final XMLEncoder enc = encoder(sink.output());
        try {
            enc.setExceptionListener(ztl);
            enc.writeObject(obj);
        } finally {
            enc.close(); // may pass exception to ztl!
        }
        ztl.check();
    }

    /** Returns a new XML encoder. */
    protected XMLEncoder encoder(OutputStream out) {
        return new XMLEncoder(out);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> T decode(final Source source, final Type expected)
    throws Exception {
        final Object obj;
        final ZeroToleranceListener ztl = new ZeroToleranceListener();
        final XMLDecoder dec = decoder(source.input());
        try {
            dec.setExceptionListener(ztl);
            obj = dec.readObject();
        } finally {
            dec.close(); // may pass exception to ztl!
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
            assert null != ex;
            if (null == this.ex) this.ex = ex; // don't overwrite prior exception
        }

        void check() throws Exception { if (null != ex) throw ex; }
    } // ZeroToleranceListener
}

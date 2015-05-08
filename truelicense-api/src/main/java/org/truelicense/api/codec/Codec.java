/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.codec;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;

import java.lang.reflect.Type;

/**
 * Defines an object graph encoding/decoding (alias serialization).
 *
 * @author Christian Schlichtherle
 */
public interface Codec {

    /**
     * Returns an identifier for the content type used by this codec.
     * The returned string must conform to the syntax specified in
     * <a href="http://tools.ietf.org/html/rfc2045">RFC2045</a>
     * "Multipurpose Internet Mail Extensions (MIME) Part One: Format of Internet Message Bodies",
     * <a href="http://tools.ietf.org/html/rfc2045#section-5.1">Section 5.1</a>
     * "Syntax of the Content-Type Header Field",
     * except that it must not start with "Content-Type:"
     * and optional spaces, i.e. the field name is stripped.
     */
    String contentType();

    /**
     * Returns an identifier for the content transfer encoding used by this
     * codec.
     * The returned string must conform to the syntax specified in
     * <a href="http://tools.ietf.org/html/rfc2045">RFC2045</a>
     * "Multipurpose Internet Mail Extensions (MIME) Part One: Format of Internet Message Bodies",
     * <a href="http://tools.ietf.org/html/rfc2045#section-6.1">Section 6.1</a>
     * "Content-Transfer-Encoding Syntax",
     * except that it must not start with "Content-Transfer-Encoding:"
     * and optional spaces, i.e. the field name must be stripped.
     * <p>
     * If the Content-Transfer-Encoding equals (ignoring case) {@code "8bit"}
     * and the  {@linkplain #contentType Content-Type} does not specify a
     * {@code charset} parameter, then {@code UTF-8} is assumed as the charset.
     */
    String contentTransferEncoding();

    /** Returns an encoder for the given sink. */
    Encoder encoder(Sink sink);

    /** Returns a decoder for the given source. */
    Decoder decoder(Source source);
}

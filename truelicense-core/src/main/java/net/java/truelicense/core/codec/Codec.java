/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.codec;

import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.java.truelicense.core.io.*;

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

    /**
     * Encodes a nullable object graph to the given sink.
     *
     * @param sink the sink to write the encoded object graph to.
     * @param obj the nullable object graph.
     *        Implementations should support encoding {@code null}.
     *        If they do not support this, then this should be documented in
     *        the Javadoc.
     */
    void encode(Sink sink, @Nullable Object obj) throws Exception;

    /**
     * Decodes a nullable object graph from the given source.
     *
     * @param  <T> the expected generic type of the decoded object.
     * @param  source the source from where to read the encoded object graph
     *         from.
     * @param  expected the expected generic type of the decoded object graph,
     *         e.g. {@code String.class}.
     *         This is just a hint and the implementation may ignore it.
     * @return A duplicate of the original object graph.
     *         Its actual type may differ from the expected generic type.
     *         It may be {@code null} if and only if the original object graph
     *         was {@code null}.
     */
    @Nullable <T> T decode(Source source, Type expected) throws Exception;
}

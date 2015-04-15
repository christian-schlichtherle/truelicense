/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.codec;

import java.nio.charset.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckForNull;

import net.java.truelicense.core.io.MemoryStore;
import net.java.truelicense.core.io.Store;
import net.java.truelicense.obfuscate.Obfuscate;
import org.apache.commons.codec.Charsets;

/**
 * Provides common {@link Codec} functions.
 *
 * @author Christian Schlichtherle
 */
public class Codecs {

    @Obfuscate static final String APPLICATION_XML_WITH_UTF_8 = "application/xml; charset=utf-8";

    @Obfuscate static final String SEVEN_BIT = "7bit";
    @Obfuscate static final String EIGHT_BIT = "8bit";
    @Obfuscate static final String QUOTED_PRINTABLE = "quoted-printable";
    @Obfuscate static final String BASE64 = "base64";

    /** Not perfect, but should work for all valid input. */
    @Obfuscate private static final String CHARSET_REGEXP =
            "\\s*charset\\s*=\\s*(?:\"([^\"]*)\"|([^\\s\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]+))";

    private static final Pattern CHARSET_PATTERN =
            Pattern.compile(CHARSET_REGEXP, Pattern.CASE_INSENSITIVE);

    /**
     * Figures the content transfer charset which is used by the given codec.
     * This method passes the call to {@link #contentTransferCharset}.
     * If an {@link IllegalArgumentException} is thrown, then it returns
     * {@code null}.
     *
     * @param  codec the codec to test.
     * @return The content transfer charset which is used by the given codec or
     *         {@code null} if the codec doesn't produce text or specifies an
     *         invalid charset name or an unknown charset.
     */
    public static @CheckForNull Charset charset(final Codec codec) {
        try { return contentTransferCharset(codec); }
        catch (IllegalArgumentException ex) { return null; }
    }

    /**
     * Figures the content transfer charset which is used by the given codec.
     * <ol>
     * <li>
     * First, the codec's
     * {@linkplain Codec#contentTransferEncoding Content-Transfer-Encoding}
     * gets tested:
     * If it is {@code "7bit"}, {@code "quoted-printable"} or {@code "base64"}
     * (ignoring case), then the returned charset is US-ASCII.
     * <li>
     * Otherwise, if the codec's Content-Transfer-Encoding is {@code "8bit"}
     * (ignoring case), then the codec's {@link Codec#contentType Content-Type}
     * gets tested:
     * <ol>
     * <li>
     * If the codec's Content-Type specifies a {@code charset} parameter
     * (ignoring case), then an attempt is made to load this charset.
     * Upon success, this charset gets returned.
     * <li>
     * If the codec's Content-Type does not specify a {@code charset} parameter,
     * then UTF-8 gets returned.
     * This ensures compatibility with JSON.
     * </ol>
     * <li>
     * Finally, in all other cases, an {@link IllegalArgumentException} gets
     * thrown in order to specify that the codec doesn't produce text or
     * specifies an invalid charset name or an unknown charset.
     * </ol>
     *
     * @param  codec the codec to test.
     * @return The content transfer charset which is used by the given codec.
     * @throws IllegalCharsetNameException if the specified charset name is
     *         invalid.
     * @throws UnsupportedCharsetException if the specified charset is unknown.
     * @throws BinaryCodecException if the codec doesn't produce text.
     * @since TrueLicense 2.2.1
     */
    public static Charset contentTransferCharset(final Codec codec) {
        final String encoding = codec.contentTransferEncoding();
        if (    SEVEN_BIT.equalsIgnoreCase(encoding) ||
                QUOTED_PRINTABLE.equalsIgnoreCase(encoding) ||
                BASE64.equalsIgnoreCase(encoding)) {
            return Charsets.US_ASCII;
        } else  if (EIGHT_BIT.equalsIgnoreCase(encoding)) {
            final Matcher matcher = CHARSET_PATTERN.matcher(codec.contentType());
            if (matcher.find()) {
                assert 2 == matcher.groupCount();
                String charset = matcher.group(1);
                if (null == charset) charset = matcher.group(2);
                return Charset.forName(charset);
            } else {
                return Charsets.UTF_8;
            }
        }
        throw new BinaryCodecException(
                "The Content-Transfer-Encoding " + encoding + " doesn't produce text.");
    }

    /**
     * Returns a clone of the given object using a new
     * {@link SerializationCodec}.
     *
     * @since TrueLicense 2.5
     */
    public static <T> T clone(T object) throws Exception {
        return clone(object, new SerializationCodec());
    }

    /**
     * Returns a clone of the given object using the given codec.
     *
     * @since TrueLicense 2.5
     */
    public static <T> T clone(final T object, final Codec codec) throws Exception {
        final Store store = new MemoryStore();
        codec.encode(store, object);
        return codec.decode(store, object.getClass());
    }

    private Codecs() { }
}

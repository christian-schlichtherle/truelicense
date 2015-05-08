/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.codec;

import org.truelicense.api.codec.Codec;
import org.truelicense.api.io.Store;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.spi.io.MemoryStore;
import org.truelicense.spi.misc.Option;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides common {@link Codec} functions.
 *
 * @author Christian Schlichtherle
 */
public class Codecs {

    @Obfuscate private static final String SEVEN_BIT = "7bit";
    @Obfuscate private static final String EIGHT_BIT = "8bit";
    @Obfuscate private static final String QUOTED_PRINTABLE = "quoted-printable";
    @Obfuscate private static final String BASE64 = "base64";

    /** Not perfect, but should work for all valid input. */
    @Obfuscate private static final String CHARSET_REGEXP =
            "\\s*charset\\s*=\\s*(?:\"([^\"]*)\"|([^\\s\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]+))";

    private static final Pattern CHARSET_PATTERN =
            Pattern.compile(CHARSET_REGEXP, Pattern.CASE_INSENSITIVE);

    /**
     * Figures the content transfer charset which is used by the given codec.
     * This method passes the call to {@link #contentTransferCharset}.
     *
     * @param  codec the codec to test.
     * @return A list which either contains the single content transfer charset
     *         which is used by the given codec or is empty if the codec
     *         doesn't produce text or specifies an invalid charset name or an
     *         unknown charset.
     */
    public static List<Charset> charset(final Codec codec) {
        try { return Collections.singletonList(contentTransferCharset(codec)); }
        catch (IllegalArgumentException ex) { return Collections.emptyList(); }
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
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public static Charset contentTransferCharset(final Codec codec) {
        final String encoding = codec.contentTransferEncoding();
        if (    SEVEN_BIT.equalsIgnoreCase(encoding) ||
                QUOTED_PRINTABLE.equalsIgnoreCase(encoding) ||
                BASE64.equalsIgnoreCase(encoding)) {
            return StandardCharsets.US_ASCII;
        } else  if (EIGHT_BIT.equalsIgnoreCase(encoding)) {
            final Matcher matcher = CHARSET_PATTERN.matcher(codec.contentType());
            if (matcher.find()) {
                assert 2 == matcher.groupCount();
                for (String charset : Option.wrap(matcher.group(1)))
                    return Charset.forName(charset);
                return Charset.forName(matcher.group(2));
            } else {
                return StandardCharsets.UTF_8;
            }
        }
        throw new BinaryCodecException(
                "The Content-Transfer-Encoding " + encoding + " doesn't produce text.");
    }

    /**
     * Returns a clone of the given object using a new
     * {@link SerializationCodec}.
     */
    public static <T> T clone(T object) throws Exception {
        return clone(object, new SerializationCodec());
    }

    /**
     * Returns a clone of the given object using the given codec.
     */
    public static <T> T clone(final T object, final Codec codec) throws Exception {
        final Store store = new MemoryStore();
        codec.encoder(store).encode(object);
        return codec.decoder(store).decode(object.getClass());
    }

    private Codecs() { }
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.codec;

import net.truelicense.api.codec.Codec;
import net.truelicense.obfuscate.Obfuscate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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

    // Not perfect, but should work for all valid input.
    @Obfuscate private static final String CHARSET_REGEXP =
            "\\s*charset\\s*=\\s*(?:\"([^\"]*)\"|([^\\s()<>@,;:\\\\\"/\\[\\]?=]+))";

    private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEXP, Pattern.CASE_INSENSITIVE);

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
     * Finally, in all other cases, the optional return value is empty in order to specify that the codec doesn't
     * produce text or specifies an invalid charset name or an unknown charset.
     * </ol>
     *
     * @param  codec the codec to test.
     * @return The optional content transfer charset which is used by the given codec.
     *         Maybe empty if the codec doesn't produce text.
     * @throws IllegalArgumentException if the codec specifies and invalid charset name or an unknown charset.
     * @since TrueLicense 3.1.0
     */
    public static Optional<Charset> charset(final Codec codec) {
        final String encoding = codec.contentTransferEncoding();
        if (SEVEN_BIT.equalsIgnoreCase(encoding) ||
                QUOTED_PRINTABLE.equalsIgnoreCase(encoding) ||
                BASE64.equalsIgnoreCase(encoding)) {
            return Optional.of(StandardCharsets.US_ASCII);
        } else if (EIGHT_BIT.equalsIgnoreCase(encoding)) {
            final Matcher matcher = CHARSET_PATTERN.matcher(codec.contentType());
            if (matcher.find()) {
                assert 2 == matcher.groupCount();
                return Optional.of(Optional
                        .ofNullable(matcher.group(1))
                        .map(Charset::forName)
                        .orElseGet(() -> Charset.forName(matcher.group(2))));
            } else {
                return Optional.of(StandardCharsets.UTF_8);
            }
        }
        return Optional.empty();
    }

    private Codecs() { }
}

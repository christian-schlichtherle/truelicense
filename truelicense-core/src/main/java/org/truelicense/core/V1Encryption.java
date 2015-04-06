/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import java.io.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.Callable;
import javax.annotation.concurrent.Immutable;
import javax.crypto.*;
import static javax.crypto.Cipher.*;
import javax.crypto.spec.*;

import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.core.crypto.*;
import org.truelicense.obfuscate.Obfuscate;

/**
 * The encryption for V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class V1Encryption extends BasicPbeEncryption {

    @Obfuscate private static final String PBE_ALGORITHM = "PBEWithMD5andDES";

    @Obfuscate
    private static final String
            ILLEGAL_PBE_ALGORITHM = "V1 format license keys require the " + PBE_ALGORITHM + " algorithm.";

    V1Encryption(final PbeParameters pbe) {
        super(pbe);
        if (!PBE_ALGORITHM.equalsIgnoreCase(pbe.algorithm()))
            throw new IllegalArgumentException(ILLEGAL_PBE_ALGORITHM);
    }

    @Override public Sink apply(final Sink sink) {
        return new Sink() {
            @Override public OutputStream output() throws IOException {
                return wrap(new Callable<OutputStream>() {
                    @Override public OutputStream call() throws Exception {
                        final Cipher cipher = cipher(true);
                        return new CipherOutputStream(sink.output(), cipher);
                    }
                });
            }
        };
    }

    @Override public Source unapply(final Source source) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return wrap(new Callable<InputStream>() {
                    @Override public InputStream call() throws Exception {
                        final Cipher cipher = cipher(false);
                        return new CipherInputStream(source.input(), cipher);
                    }
                });
            }
        };
    }

    private Cipher cipher(final boolean encrypt) throws Exception {
        // Hard coded in TrueLicense V1.
        final AlgorithmParameterSpec spec = new PBEParameterSpec(
                new byte[] {
                    (byte) 0xce, (byte) 0xfb, (byte) 0xde, (byte) 0xac,
                    (byte) 0x05, (byte) 0x02, (byte) 0x19, (byte) 0x71
                },
                2005);
        final Cipher cipher = getInstance(algorithm());
        cipher.init(encrypt ? ENCRYPT_MODE : DECRYPT_MODE, secretKey(), spec);
        return cipher;
    }
}

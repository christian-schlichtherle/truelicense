/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1.crypto;

import org.truelicense.api.crypto.EncryptionParameters;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.passwd.PasswordUsage;
import org.truelicense.core.crypto.BasicEncryption;
import org.truelicense.obfuscate.Obfuscate;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;

import static javax.crypto.Cipher.*;

/**
 * The encryption for V1 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V1Encryption extends BasicEncryption {

    @Obfuscate private static final String PBE_ALGORITHM = "PBEWithMD5andDES";

    @Obfuscate
    private static final String
            ILLEGAL_PBE_ALGORITHM = "V1 format license keys require the " + PBE_ALGORITHM + " algorithm.";

    public V1Encryption(final EncryptionParameters parameters) {
        super(parameters);
        if (!PBE_ALGORITHM.equalsIgnoreCase(parameters.algorithm()))
            throw new IllegalArgumentException(ILLEGAL_PBE_ALGORITHM);
    }

    @Override public Sink apply(final Sink sink) {
        return () -> wrap(() -> {
            final Cipher cipher = cipher(PasswordUsage.WRITE);
            return new CipherOutputStream(sink.output(), cipher);
        });
    }

    @Override public Source unapply(final Source source) {
        return () -> wrap(() -> {
            final Cipher cipher = cipher(PasswordUsage.READ);
            return new CipherInputStream(source.input(), cipher);
        });
    }

    private Cipher cipher(final PasswordUsage usage) throws Exception {
        // Hard coded in TrueLicense V1.
        final AlgorithmParameterSpec spec = new PBEParameterSpec(
                new byte[] {
                    (byte) 0xce, (byte) 0xfb, (byte) 0xde, (byte) 0xac,
                    (byte) 0x05, (byte) 0x02, (byte) 0x19, (byte) 0x71
                },
                2005);
        final Cipher cipher = getInstance(algorithm());
        cipher.init(
                PasswordUsage.WRITE.equals(usage) ? ENCRYPT_MODE : DECRYPT_MODE,
                secretKey(usage), spec);
        return cipher;
    }
}

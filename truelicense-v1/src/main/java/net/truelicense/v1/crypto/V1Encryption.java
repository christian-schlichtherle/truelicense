/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v1.crypto;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Transformation;
import global.namespace.fun.io.bios.BIOS;
import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.passwd.PasswordUsage;
import net.truelicense.core.crypto.BasicEncryption;
import net.truelicense.obfuscate.Obfuscate;

import javax.crypto.Cipher;
import javax.crypto.spec.PBEParameterSpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;

import static javax.crypto.Cipher.*;

/**
 * The encryption for V1 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V1Encryption extends BasicEncryption {

    @Obfuscate
    private static final String PBE_ALGORITHM = "PBEWithMD5AndDES";

    @Obfuscate
    private static final String
            ILLEGAL_PBE_ALGORITHM = "V1 format license keys require the " + PBE_ALGORITHM + " algorithm.";

    private final Transformation cipher = BIOS.cipher(this::cipher);

    public V1Encryption(final EncryptionParameters parameters) {
        super(parameters);
        if (!PBE_ALGORITHM.equalsIgnoreCase(parameters.algorithm())) {
            throw new IllegalArgumentException(ILLEGAL_PBE_ALGORITHM);
        }
    }

    @Override
    public Socket<OutputStream> apply(Socket<OutputStream> output) { return cipher.apply(output); }

    @Override
    public Socket<InputStream> unapply(Socket<InputStream> input) { return cipher.unapply(input); }

    private Cipher cipher(boolean forEncryption) throws Exception {
        return cipher(forEncryption ? PasswordUsage.WRITE : PasswordUsage.READ);
    }

    private Cipher cipher(final PasswordUsage usage) throws Exception {
        // The salt is hard coded in TrueLicense V1:
        final AlgorithmParameterSpec spec = new PBEParameterSpec(
                new byte[] {
                    (byte) 0xce, (byte) 0xfb, (byte) 0xde, (byte) 0xac,
                    (byte) 0x05, (byte) 0x02, (byte) 0x19, (byte) 0x71
                },
                2005);
        final Cipher cipher = getInstance(algorithm());
        cipher.init(PasswordUsage.WRITE.equals(usage) ? ENCRYPT_MODE : DECRYPT_MODE, secretKey(usage), spec);
        return cipher;
    }

    @Override
    public Transformation inverse() { return cipher.inverse(); }
}

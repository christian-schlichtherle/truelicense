/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.core;

import global.namespace.fun.io.api.Socket;
import global.namespace.truelicense.core.crypto.EncryptionMixin;
import global.namespace.truelicense.api.crypto.Encryption;
import global.namespace.truelicense.api.crypto.EncryptionParameters;
import global.namespace.truelicense.api.passwd.PasswordUsage;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;

import static javax.crypto.Cipher.*;

/**
 * The password based encryption for V2 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class V2Encryption extends EncryptionMixin implements Encryption {

    V2Encryption(EncryptionParameters parameters) {
        super(parameters);
    }

    @Override
    public Socket<OutputStream> output(final Socket<OutputStream> output) {
        return output.map(out -> {
            final Cipher cipher = cipher(PasswordUsage.WRITE, null);
            final AlgorithmParameters param = cipher.getParameters();
            final byte[] encoded = param.getEncoded();
            assert encoded.length <= Short.MAX_VALUE;
            new DataOutputStream(out).writeShort(encoded.length);
            out.write(encoded);
            return new CipherOutputStream(out, cipher);
        });
    }

    @Override
    public Socket<InputStream> input(final Socket<InputStream> input) {
        return input.map(in -> {
            final DataInputStream din = new DataInputStream(in);
            final byte[] encoded = new byte[din.readShort() & 0xffff];
            din.readFully(encoded);
            return new CipherInputStream(in, cipher(PasswordUsage.READ, param(encoded)));
        });
    }

    private Cipher cipher(final PasswordUsage usage, final AlgorithmParameters param) throws Exception {
        final Cipher cipher = getInstance(algorithm());
        cipher.init(PasswordUsage.WRITE.equals(usage) ? ENCRYPT_MODE : DECRYPT_MODE, secretKey(usage), param);
        return cipher;
    }

    private AlgorithmParameters param(final byte[] encoded) throws Exception {
        final AlgorithmParameters param = AlgorithmParameters.getInstance(algorithm());
        param.init(encoded);
        return param;
    }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.commons;

import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.passwd.PasswordUsage;
import org.truelicense.core.crypto.BasicPbeEncryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.security.AlgorithmParameters;
import java.util.concurrent.Callable;

import static javax.crypto.Cipher.*;

/**
 * The encryption for V2 format license keys.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V2Encryption extends BasicPbeEncryption {

    public V2Encryption(PbeParameters pbe) { super(pbe); }

    @Override
    public Sink apply(final Sink sink) {
        return new Sink() {
            @Override public OutputStream output() throws IOException {
                return wrap(new Callable<OutputStream>() {
                    @Override public OutputStream call() throws Exception {
                        final Cipher cipher = cipher(PasswordUsage.WRITE, null);
                        final AlgorithmParameters
                                param = cipher.getParameters();
                        final byte[] encoded = param.getEncoded();
                        assert encoded.length <= Short.MAX_VALUE;
                        final OutputStream out = sink.output();
                        try {
                            new DataOutputStream(out).writeShort(encoded.length);
                            out.write(encoded);
                        } catch (final Throwable t) {
                            try { out.close(); }
                            catch (Throwable t2) { t.addSuppressed(t2); }
                            throw t;
                        }
                        return new CipherOutputStream(out, cipher);
                    }
                });
            }
        };
    }

    @Override
    public Source unapply(final Source source) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return wrap(new Callable<InputStream>() {
                    @Override public InputStream call() throws Exception {
                        final Cipher cipher;
                        final InputStream in = source.input();
                        try {
                            final DataInputStream din = new DataInputStream(in);
                            final byte[] encoded = new byte[din.readShort() & 0xffff];
                            din.readFully(encoded);
                            cipher = cipher(PasswordUsage.READ, param(encoded));
                        } catch (final Throwable t) {
                            try { in.close(); }
                            catch (Throwable t2) { t.addSuppressed(t2); }
                            throw t;
                        }
                        return new CipherInputStream(in, cipher);
                    }
                });
            }
        };
    }

    private AlgorithmParameters param(final byte[] encoded) throws Exception {
        final AlgorithmParameters
                param = AlgorithmParameters.getInstance(algorithm());
        param.init(encoded);
        return param;
    }

    private Cipher cipher(final PasswordUsage usage, final AlgorithmParameters param) throws Exception {
        final Cipher cipher = getInstance(algorithm());
        cipher.init(
                PasswordUsage.WRITE.equals(usage) ? ENCRYPT_MODE : DECRYPT_MODE,
                secretKey(usage), param);
        return cipher;
    }
}

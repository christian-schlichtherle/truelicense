/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.*;
import java.security.AlgorithmParameters;
import java.util.concurrent.Callable;
import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;
import javax.crypto.*;
import static javax.crypto.Cipher.*;
import org.truelicense.core.crypto.*;
import org.truelicense.core.io.*;

/**
 * The encryption for V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class V2Encryption extends BasicPbeEncryption {

    V2Encryption(PbeParameters pbe) { super(pbe); }

    @Override public Sink apply(final Sink sink) {
        return new Sink() {
            @Override public OutputStream output() throws IOException {
                return wrap(new Callable<OutputStream>() {
                    @SuppressFBWarnings("OS_OPEN_STREAM")
                    @Override public OutputStream call() throws Exception {
                        final Cipher cipher = cipher(true, null);
                        final AlgorithmParameters
                                param = cipher.getParameters();
                        final byte[] encoded = param.getEncoded();
                        assert encoded.length <= Short.MAX_VALUE;
                        final OutputStream out = sink.output();
                        try {
                            new DataOutputStream(out).writeShort(encoded.length);
                            out.write(encoded);
                        } catch (final Exception ex) { // TODO: make this a Throwable for Java 7
                            try { out.close(); }
                            finally { throw ex; }
                        }
                        return new CipherOutputStream(out, cipher);
                    }
                });
            }
        };
    }

    @Override public Source unapply(final Source source) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return wrap(new Callable<InputStream>() {
                    @SuppressFBWarnings("OS_OPEN_STREAM")
                    @Override public InputStream call() throws Exception {
                        final Cipher cipher;
                        final InputStream in = source.input();
                        try {
                            final DataInputStream din = new DataInputStream(in);
                            final byte[] encoded = new byte[din.readShort() & 0xffff];
                            din.readFully(encoded);
                            cipher = cipher(false, param(encoded));
                        } catch (final Exception ex) { // TODO: make this a Throwable for Java 7
                            try { in.close(); }
                            finally { throw ex; }
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

    private Cipher cipher(
            final boolean encrypt,
            final @CheckForNull AlgorithmParameters param)
    throws Exception {
        final Cipher cipher = getInstance(algorithm());
        cipher.init(encrypt ? ENCRYPT_MODE : DECRYPT_MODE, secretKey(), param);
        return cipher;
    }
}

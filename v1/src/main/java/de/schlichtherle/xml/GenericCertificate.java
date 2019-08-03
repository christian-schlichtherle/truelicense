/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package de.schlichtherle.xml;

/**
 * A repository model for use with V1 format license keys.
 */
public final class GenericCertificate {

    private String encoded, signature, signatureAlgorithm, signatureEncoding;

    public String getEncoded() { return encoded; }

    public void setEncoded(final String encoded) {
        this.encoded = encoded;
    }

    public String getSignature() { return signature; }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignatureAlgorithm()  { return signatureAlgorithm; }

    public void setSignatureAlgorithm(final String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getSignatureEncoding() { return signatureEncoding; }

    public void setSignatureEncoding(final String signatureEncoding) {
        this.signatureEncoding = signatureEncoding;
    }
}

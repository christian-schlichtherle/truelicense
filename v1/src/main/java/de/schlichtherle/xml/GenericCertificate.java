/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package de.schlichtherle.xml;

import global.namespace.truelicense.core.misc.Strings;

import java.util.Objects;

import static java.util.Locale.ENGLISH;

/**
 * A repository model for use with V1 format license keys.
 * All properties are set to {@code null} by default.
 *
 * @author Christian Schlichtherle
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

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GenericCertificate)) return false;
        final GenericCertificate that = (GenericCertificate) obj;
        return  Objects.equals(this.getEncoded(), that.getEncoded()) &&
                Objects.equals(this.getSignature(), that.getSignature()) &&
                Strings.equalsIgnoreCase(this.getSignatureAlgorithm(), that.getSignatureAlgorithm());
    }

    @Override
    public int hashCode() {
        int c = 17;
        c = 31 * c + Objects.hashCode(getEncoded());
        c = 31 * c + Objects.hashCode(getSignature());
        c = 31 * c + Objects.hashCode(Strings.toLowerCase(getSignatureAlgorithm(), ENGLISH));
        return c;
    }
}

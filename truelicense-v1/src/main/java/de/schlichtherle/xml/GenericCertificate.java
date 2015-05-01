/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package de.schlichtherle.xml;

import org.truelicense.api.auth.Artifactory;
import org.truelicense.api.auth.Repository;
import org.truelicense.api.codec.Codec;
import org.truelicense.core.auth.BasicRepository;
import org.truelicense.obfuscate.Obfuscate;

import java.security.Signature;

/**
 * Provides compatibility with V1 format license keys.
 * All properties are set to {@code null} by default.
 *
 * @author Christian Schlichtherle
 */
public final class GenericCertificate implements Repository {

    @Obfuscate private static final String SIGNATURE_ENCODING = "US-ASCII/Base64";

    private final BasicRepository repository = new BasicRepository();

    private String signatureEncoding;

    public String getEncoded()   {
        return repository.getArtifact();
    }

    public void setEncoded(String encoded) {
        repository.setArtifact(encoded);
    }

    public String getSignatureAlgorithm()  {
        return repository.getAlgorithm();
    }

    public void setSignatureAlgorithm(String algorithm) {
        repository.setAlgorithm(algorithm);
    }

    public String getSignatureEncoding() { return signatureEncoding; }

    public void setSignatureEncoding(final String signatureEncoding) {
        this.signatureEncoding = signatureEncoding;
    }

    public String getSignature() {
        return repository.getSignature();
    }

    public void setSignature(String signature) {
        repository.setSignature(signature);
    }

    @Override
    public Artifactory sign(final Codec codec, final Signature engine, final Object artifact) throws Exception {
        final Artifactory a = repository.sign(codec, engine, artifact);
        setSignatureEncoding(SIGNATURE_ENCODING);
        return a;
    }

    @Override
    public Artifactory verify(Codec codec, Signature engine) throws Exception {
        return repository.verify(codec, engine);
    }

    @Override
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GenericCertificate)) return false;
        final GenericCertificate that = (GenericCertificate) obj;
        return this.repository.equals(that.repository);
    }

    @Override
    public int hashCode() {
        return repository.hashCode();
    }
}

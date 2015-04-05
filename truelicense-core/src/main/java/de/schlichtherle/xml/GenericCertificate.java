/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package de.schlichtherle.xml;

import org.truelicense.core.auth.Artifactory;
import org.truelicense.core.auth.BasicRepository;
import org.truelicense.core.auth.Repository;
import org.truelicense.core.auth.RepositoryModel;
import org.truelicense.core.codec.Codec;
import org.truelicense.obfuscate.Obfuscate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Provides compatibility with version 1 (V1) format license keys as used
 * by TrueLicense 1.X applications.
 * This type of repositoryProvider is used in V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
@ParametersAreNullableByDefault
@Nullable
public final class GenericCertificate implements Repository {

    @Obfuscate private static final String SIGNATURE_ENCODING = "US-ASCII/Base64";

    private final BasicRepository repository = new BasicRepository();

    private String signatureEncoding;

    @Override public RepositoryModel model() { return repository; }

    public String getEncoded()   {
        return repository.getArtifact();
    }

    public void setEncoded(final String encoded) {
        repository.setArtifact(encoded);
    }

    public String getSignatureAlgorithm()  {
        return repository.getAlgorithm();
    }

    public void setSignatureAlgorithm(final String algorithm) {
        repository.setAlgorithm(algorithm);
    }

    public String getSignatureEncoding() { return signatureEncoding; }

    public void setSignatureEncoding(final String signatureEncoding) {
        this.signatureEncoding = signatureEncoding;
    }

    public String getSignature() {
        return repository.getSignature();
    }

    public void setSignature(final String signature) {
        repository.setSignature(signature);
    }

    @Override
    public Artifactory sign(
            final Codec codec,
            final Signature engine,
            final PrivateKey key,
            final @Nullable Object artifact)
    throws Exception {
        final Artifactory a = repository.sign(codec, engine, key, artifact);
        setSignatureEncoding(SIGNATURE_ENCODING);
        return a;
    }

    @Override
    public Artifactory verify(
            Codec codec,
            Signature engine,
            PublicKey key)
    throws Exception {
        return repository.verify(codec, engine, key);
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

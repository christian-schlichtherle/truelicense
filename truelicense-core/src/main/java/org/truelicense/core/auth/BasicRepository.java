/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.auth;

import org.apache.commons.codec.binary.Base64;
import org.truelicense.api.auth.Artifactory;
import org.truelicense.api.auth.Repository;
import org.truelicense.api.auth.RepositoryIntegrityException;
import org.truelicense.api.codec.Codec;
import org.truelicense.spi.io.MemoryStore;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.nio.charset.Charset;
import java.security.Signature;

import static org.truelicense.spi.codec.Codecs.charset;

/**
 * A basic repository for storing authenticated objects (artifacts).
 * This type of repository is used in V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
// #TRUELICENSE-50: The XML element name MUST be explicitly defined!
// Otherwise, it would get derived from the class name, but this would break
// if the class name gets obfuscated, e.g. when using the ProGuard
// configuration from the TrueLicense Maven Archetype.
@XmlRootElement(name = "repository")
// #TRUELICENSE-52: Dito for the XML type. This enables objects of this class
// to participate in larger object graphs which the application wants to
// encode/decode to/from XML.
@XmlType(name = "repository")
public final class BasicRepository
extends RepositoryModel implements Repository {

    private final Base64 base64 = new Base64();
    
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private byte[] data(final Codec codec, final String body) {
        for (Charset cs : charset(codec))
            return body.getBytes(cs);
        return decode(body);
    }

    private byte[] decode(String body) { return base64.decode(body); }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private String body(final Codec codec, final byte[] artifact) {
        for (Charset cs : charset(codec))
            return new String(artifact, cs);
        return encode(artifact);
    }

    private String encode(byte[] data) { return base64.encodeToString(data); }

    @Override
    public final Artifactory sign(final Codec codec, final Signature engine, final Object artifact) throws Exception {
        final MemoryStore store = new MemoryStore();
        codec.encode(store, artifact);
        final byte[] artifactData = store.data();
        engine.update(artifactData);
        final byte[] signatureData = engine.sign();

        final String encodedArtifact = body(codec, artifactData);
        final String encodedSignature = encode(signatureData);
        final String signatureAlgorithm = engine.getAlgorithm();
        final Artifactory artifactory = new EncodedArtifact(codec, store);

        setArtifact(encodedArtifact);
        setSignature(encodedSignature);
        setAlgorithm(signatureAlgorithm);

        return artifactory;
    }

    @Override
    public final Artifactory verify(final Codec codec, final Signature engine) throws Exception {
        if (!engine.getAlgorithm().equalsIgnoreCase(getAlgorithm()))
            throw new IllegalArgumentException();
        final byte[] artifactData = data(codec, getArtifact());
        engine.update(artifactData);
        if (!engine.verify(decode(getSignature())))
            throw new RepositoryIntegrityException();
        return new EncodedArtifact(codec, artifactData);
    }
}

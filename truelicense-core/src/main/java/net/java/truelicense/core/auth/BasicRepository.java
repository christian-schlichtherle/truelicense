/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.auth;

import net.java.truelicense.core.codec.Codec;
import net.java.truelicense.core.io.MemoryStore;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import static net.java.truelicense.core.codec.Codecs.contentTransferCharset;

/**
 * A basic repository for storing authenticated objects (artifacts).
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

    private static final Base64 base64 = new Base64();

    @Override public final RepositoryModel model() { return this; }

    private @Nullable byte[] data(
            final Codec codec,
            final @CheckForNull String body) {
        if (null == body) return null;
        try { return body.getBytes(contentTransferCharset(codec)); }
        catch (IllegalArgumentException ex) { return decode(body); }
    }

    private static @Nullable byte[] decode(@CheckForNull String body) {
        return base64.decode(body);
    }

    private @Nullable String body(
            final Codec codec,
            final @CheckForNull byte[] artifact) {
        if (null == artifact) return null;
        try { return new String(artifact, contentTransferCharset(codec)); }
        catch (IllegalArgumentException ex) { return encode(artifact); }
    }

    private static @Nullable String encode(@CheckForNull byte[] data) {
        return base64.encodeToString(data);
    }

    @Override public final Artifactory sign(
            final Codec codec,
            final Signature engine,
            final PrivateKey key,
            final @Nullable Object artifact)
    throws Exception {
        final MemoryStore store = new MemoryStore();
        codec.encode(store, artifact);
        final byte[] artifactData = store.data();
        engine.initSign(key);
        engine.update(artifactData);
        final byte[] signatureData = engine.sign();

        final String encodedArtifact = body(codec, artifactData);
        final String encodedSignature = encode(signatureData);
        final String signatureAlgorithm = engine.getAlgorithm();
        final Artifactory artifactory = new EncodedArtifact(codec, store);

        final RepositoryModel model = model();
        model.setArtifact(encodedArtifact);
        model.setSignature(encodedSignature);
        model.setAlgorithm(signatureAlgorithm);

        return artifactory;
    }

    @Override public final Artifactory verify(
            final Codec codec,
            final Signature engine,
            final PublicKey key)
    throws Exception {
        final RepositoryModel model = model();
        if (!engine.getAlgorithm().equalsIgnoreCase(model.getAlgorithm()))
            throw new IllegalArgumentException();
        engine.initVerify(key);
        final byte[] artifactData = data(codec, model.getArtifact());
        engine.update(artifactData);
        if (!engine.verify(decode(model.getSignature())))
            throw new RepositoryIntegrityException();
        return new EncodedArtifact(codec, artifactData);
    }
}

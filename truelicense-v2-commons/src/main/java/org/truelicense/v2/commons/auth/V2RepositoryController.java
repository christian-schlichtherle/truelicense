/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.commons.auth;

import org.truelicense.api.auth.RepositoryController;
import org.truelicense.api.auth.RepositoryIntegrityException;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.codec.Decoder;
import org.truelicense.api.io.Source;
import org.truelicense.spi.io.MemoryStore;

import java.nio.charset.Charset;
import java.security.Signature;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.Objects.requireNonNull;
import static org.truelicense.spi.codec.Codecs.charset;

/**
 * A repository controller for use with V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V2RepositoryController implements RepositoryController {

    private final V2RepositoryModel model;
    private final Codec codec;

    public V2RepositoryController(final V2RepositoryModel model, final Codec codec) {
        this.model = requireNonNull(model);
        this.codec = requireNonNull(codec);
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private byte[] data(final Codec codec, final String body) {
        for (Charset cs : charset(codec))
            return body.getBytes(cs);
        return getDecoder().decode(body);
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private String body(final Codec codec, final byte[] artifact) {
        for (Charset cs : charset(codec))
            return new String(artifact, cs);
        return getEncoder().encodeToString(artifact);
    }

    @Override
    public final Decoder sign(final Signature engine, final Object artifact) throws Exception {
        final MemoryStore store = new MemoryStore();
        codec.encoder(store).encode(artifact);
        final byte[] artifactData = store.data();
        engine.update(artifactData);
        final byte[] signatureData = engine.sign();

        final String encodedArtifact = body(codec, artifactData);
        final String encodedSignature = getEncoder().encodeToString(signatureData);
        final String signatureAlgorithm = engine.getAlgorithm();

        model.setArtifact(encodedArtifact);
        model.setSignature(encodedSignature);
        model.setAlgorithm(signatureAlgorithm);

        return codec.decoder(store);
    }

    @Override
    public final Decoder verify(final Signature engine) throws Exception {
        if (!engine.getAlgorithm().equalsIgnoreCase(model.getAlgorithm()))
            throw new IllegalArgumentException();
        final byte[] artifactData = data(codec, model.getArtifact());
        engine.update(artifactData);
        if (!engine.verify(getDecoder().decode(model.getSignature())))
            throw new RepositoryIntegrityException();
        return codec.decoder(source(artifactData));
    }

    private static Source source(final byte[] encodedArtifact) {
        final MemoryStore store = new MemoryStore();
        store.data(encodedArtifact);
        return store;
    }
}

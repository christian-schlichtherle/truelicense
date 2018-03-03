/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.commons.auth;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Store;
import global.namespace.fun.io.bios.BIOS;
import net.truelicense.api.auth.RepositoryController;
import net.truelicense.api.auth.RepositoryIntegrityException;
import net.truelicense.api.codec.Codec;

import java.security.Signature;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.Objects.requireNonNull;
import static net.truelicense.spi.codec.Codecs.charset;

/**
 * A repository controller for use with V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
final class V2RepositoryController implements RepositoryController {

    private final V2RepositoryModel model;
    private final Codec codec;

    V2RepositoryController(final V2RepositoryModel model, final Codec codec) {
        this.model = requireNonNull(model);
        this.codec = requireNonNull(codec);
    }

    @Override
    public final Decoder sign(final Signature engine, final Object artifact) throws Exception {
        final Store store = BIOS.memoryStore();
        codec.encoder(store.output()).encode(artifact);
        final byte[] artifactData = store.content();
        engine.update(artifactData);
        final byte[] signatureData = engine.sign();

        final String encodedArtifact = body(codec, artifactData);
        final String encodedSignature = getEncoder().encodeToString(signatureData);
        final String signatureAlgorithm = engine.getAlgorithm();

        model.setArtifact(encodedArtifact);
        model.setSignature(encodedSignature);
        model.setAlgorithm(signatureAlgorithm);

        return codec.decoder(store.input());
    }

    private static String body(Codec codec, byte[] artifact) {
        return charset(codec)
                .map(charset -> new String(artifact, charset))
                .orElseGet(() -> getEncoder().encodeToString(artifact));
    }

    @Override
    public final Decoder verify(final Signature engine) throws Exception {
        if (!engine.getAlgorithm().equalsIgnoreCase(model.getAlgorithm())) {
            throw new IllegalArgumentException();
        }
        final byte[] artifactData = data(codec, model.getArtifact());
        engine.update(artifactData);
        if (!engine.verify(getDecoder().decode(model.getSignature()))) {
            throw new RepositoryIntegrityException();
        }
        final Store store = BIOS.memoryStore();
        store.content(artifactData);
        return codec.decoder(store.input());
    }

    private static byte[] data(Codec codec, String body) {
        return charset(codec)
                .map(body::getBytes)
                .orElseGet(() -> getDecoder().decode(body));
    }
}

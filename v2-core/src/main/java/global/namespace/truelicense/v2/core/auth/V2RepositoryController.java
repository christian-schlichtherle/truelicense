/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.v2.core.auth;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Store;
import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.auth.RepositoryIntegrityException;
import global.namespace.truelicense.api.codec.Codec;

import java.security.Signature;

import static global.namespace.fun.io.bios.BIOS.memory;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.Objects.requireNonNull;
import static global.namespace.truelicense.spi.codec.Codecs.charset;

/**
 * A repository controller for use with V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
final class V2RepositoryController implements RepositoryController {

    private final Codec codec;
    private final V2RepositoryModel model;

    V2RepositoryController(final Codec codec, final V2RepositoryModel model) {
        this.codec = requireNonNull(codec);
        this.model = requireNonNull(model);
    }

    @Override
    public final Decoder sign(final Signature engine, final Object artifact) throws Exception {
        final Store store = memory();
        codec.encoder(store).encode(artifact);
        final byte[] artifactData = store.content();
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
        final Store store = memory();
        store.content(artifactData);
        return codec.decoder(store);
    }

    private static byte[] data(Codec codec, String body) {
        return charset(codec)
                .map(body::getBytes)
                .orElseGet(() -> getDecoder().decode(body));
    }
}

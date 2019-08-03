/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.json;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Store;
import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.auth.RepositoryIntegrityException;
import global.namespace.truelicense.api.codec.Codec;

import java.security.Signature;

import static global.namespace.fun.io.bios.BIOS.memory;
import static global.namespace.truelicense.spi.codec.Codecs.charset;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.Objects.requireNonNull;

/**
 * A repository controller for use with V2/JSON format license keys.
 */
final class V2JsonRepositoryController implements RepositoryController {

    private final Codec codec;
    private final V2JsonRepositoryModel model;

    V2JsonRepositoryController(final Codec codec, final V2JsonRepositoryModel model) {
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

        model.artifact = encodedArtifact;
        model.signature = encodedSignature;
        model.algorithm = signatureAlgorithm;

        return codec.decoder(store);
    }

    private static String body(Codec codec, byte[] artifact) {
        return charset(codec)
                .map(charset -> new String(artifact, charset))
                .orElseGet(() -> getEncoder().encodeToString(artifact));
    }

    @Override
    public final Decoder verify(final Signature engine) throws Exception {
        if (!engine.getAlgorithm().equalsIgnoreCase(model.algorithm)) {
            throw new IllegalArgumentException();
        }
        final byte[] artifactData = data(codec, model.artifact);
        engine.update(artifactData);
        if (!engine.verify(getDecoder().decode(model.signature))) {
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

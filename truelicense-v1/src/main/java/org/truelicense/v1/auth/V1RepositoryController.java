package org.truelicense.v1.auth;

import de.schlichtherle.xml.GenericCertificate;
import org.apache.commons.codec.binary.Base64;
import org.truelicense.api.auth.Artifactory;
import org.truelicense.api.auth.RepositoryController;
import org.truelicense.api.auth.RepositoryIntegrityException;
import org.truelicense.api.codec.Codec;
import org.truelicense.core.auth.EncodedArtifact;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.spi.io.MemoryStore;

import java.nio.charset.Charset;
import java.security.Signature;

import static java.util.Objects.requireNonNull;
import static org.truelicense.spi.codec.Codecs.charset;

/**
 * A repository controller for use with V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
public class V1RepositoryController implements RepositoryController {

    @Obfuscate
    private static final String SIGNATURE_ENCODING = "US-ASCII/Base64";

    private final Base64 base64 = new Base64();
    private final GenericCertificate model;
    private final Codec codec;

    public V1RepositoryController(final GenericCertificate model, final Codec codec) {
        this.model = requireNonNull(model);
        this.codec = requireNonNull(codec);
    }

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
    public final Artifactory sign(final Signature engine, final Object artifact) throws Exception {
        final MemoryStore store = new MemoryStore();
        codec.to(store).encode(artifact);
        final byte[] artifactData = store.data();
        engine.update(artifactData);
        final byte[] signatureData = engine.sign();

        final String encodedArtifact = body(codec, artifactData);
        final String encodedSignature = encode(signatureData);
        final String signatureAlgorithm = engine.getAlgorithm();
        final Artifactory artifactory = new EncodedArtifact(codec, store);

        model.setEncoded(encodedArtifact);
        model.setSignature(encodedSignature);
        model.setSignatureAlgorithm(signatureAlgorithm);
        model.setSignatureEncoding(SIGNATURE_ENCODING);

        return artifactory;
    }

    @Override
    public final Artifactory verify(final Signature engine) throws Exception {
        if (!engine.getAlgorithm().equalsIgnoreCase(model.getSignatureAlgorithm()))
            throw new IllegalArgumentException();
        final byte[] artifactData = data(codec, model.getEncoded());
        engine.update(artifactData);
        if (!engine.verify(decode(model.getSignature())))
            throw new RepositoryIntegrityException();
        return new EncodedArtifact(codec, artifactData);
    }
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core.auth;

import global.namespace.fun.io.api.Decoder;
import global.namespace.fun.io.api.Source;
import global.namespace.truelicense.api.auth.Authentication;
import global.namespace.truelicense.api.auth.AuthenticationParameters;
import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.i18n.Message;
import global.namespace.truelicense.api.passwd.Password;
import global.namespace.truelicense.api.passwd.PasswordProtection;
import global.namespace.truelicense.api.passwd.PasswordUsage;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Optional;

/**
 * Signs or verifies a generic artifact using a private or public key in a keystore entry.
 * This class is immutable.
 */
public final class Notary implements Authentication {

    @Obfuscate
    private static final String DEFAULT_ALGORITHM = "SHA1withDSA";

    @Obfuscate
    static final String NO_PRIVATE_KEY = "noPrivateKey";

    @Obfuscate
    static final String NO_CERTIFICATE = "noCertificate";

    @Obfuscate
    static final String NO_SUCH_ENTRY = "noSuchEntry";

    private final AuthenticationParameters parameters;

    public Notary(final AuthenticationParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
    }

    @Override
    public Decoder sign(RepositoryController controller, Object artifact) throws Exception {
        return new Cache().sign(controller, artifact);
    }

    @Override
    public Decoder verify(RepositoryController controller) throws Exception {
        return new Cache().verify(controller);
    }

    private AuthenticationParameters parameters() {
        return parameters;
    }

    private final class Cache {

        KeyStore keyStore;

        Decoder sign(RepositoryController controller, Object artifact) throws Exception {
            final Signature engine = engine();
            final PrivateKey key = privateKey();
            engine.initSign(key);
            return controller.sign(engine, artifact);
        }

        Decoder verify(RepositoryController controller) throws Exception {
            final Signature engine = engine();
            final PublicKey key = publicKey();
            engine.initVerify(key);
            return controller.verify(engine);
        }

        Signature engine() throws Exception {
            return Signature.getInstance(algorithm());
        }

        String algorithm() throws Exception {
            final Optional<String> configuredAlgorithm = configuredAlgorithm();
            return configuredAlgorithm.isPresent() ? configuredAlgorithm.get() : defaultAlgorithm();
        }

        String defaultAlgorithm() throws Exception {
            final Certificate cert = certificate();
            if (cert instanceof X509Certificate) {
                return ((X509Certificate) cert).getSigAlgName();
            } else {
                return DEFAULT_ALGORITHM;
            }
        }

        PrivateKey privateKey() throws Exception {
            final KeyStore.Entry entry = keyStoreEntry(PasswordUsage.ENCRYPTION);
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            } else {
                throw new NotaryException(message(NO_PRIVATE_KEY));
            }
        }

        PublicKey publicKey() throws Exception {
            return certificate().getPublicKey();
        }

        Certificate certificate() throws Exception {
            final KeyStore.Entry entry = keyStoreEntry(PasswordUsage.DECRYPTION);
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return ((KeyStore.PrivateKeyEntry) entry).getCertificate();
            } else if (entry instanceof KeyStore.TrustedCertificateEntry) {
                return ((KeyStore.TrustedCertificateEntry) entry).getTrustedCertificate();
            } else {
                throw new NotaryException(message(NO_CERTIFICATE));
            }
        }

        KeyStore.Entry keyStoreEntry(final PasswordUsage usage) throws Exception {
            if (isKeyEntry()) {
                try (Password password = keyProtection().password(usage)) {
                    final KeyStore.PasswordProtection protection =
                            new KeyStore.PasswordProtection(password.characters());
                    try {
                        return keyStoreEntry(Optional.of(protection));
                    } finally {
                        protection.destroy();
                    }
                }
            } else if (isCertificateEntry()) {
                return keyStoreEntry(Optional.empty());
            } else {
                assert !keyStore().containsAlias(alias());
                throw new NotaryException(message(NO_SUCH_ENTRY));
            }
        }

        boolean isKeyEntry() throws Exception {
            return keyStore().isKeyEntry(alias());
        }

        boolean isCertificateEntry() throws Exception {
            return keyStore().isCertificateEntry(alias());
        }

        KeyStore.Entry keyStoreEntry(Optional<KeyStore.PasswordProtection> protection) throws Exception {
            return keyStore().getEntry(alias(), protection.orElse(null));
        }

        KeyStore keyStore() throws Exception {
            final KeyStore ks = keyStore;
            return null != ks ? ks : (keyStore = newKeyStore());
        }

        KeyStore newKeyStore() throws Exception {
            try (Password password = storeProtection().password(PasswordUsage.DECRYPTION)) {
                final KeyStore keyStore = KeyStore.getInstance(storeType());
                final char[] pc = password.characters();
                final Optional<Source> source = source();
                if (source.isPresent()) {
                    source.get().acceptReader(in -> keyStore.load(in, pc));
                } else {
                    keyStore.load(null, pc);
                }
                return keyStore;
            }
        }

        Message message(String key) {
            return Messages.message(key, alias());
        }

        String alias() {
            return parameters().alias();
        }

        PasswordProtection keyProtection() {
            return parameters().keyProtection();
        }

        Optional<String> configuredAlgorithm() {
            return parameters().algorithm();
        }

        Optional<Source> source() {
            return parameters().source();
        }

        PasswordProtection storeProtection() {
            return parameters().storeProtection();
        }

        String storeType() {
            return parameters().storeType();
        }
    }
}

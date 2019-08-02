/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v4;

import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.core.Core;
import global.namespace.truelicense.obfuscate.Obfuscate;

import java.util.zip.Deflater;

import static global.namespace.fun.io.bios.BIOS.deflate;

/**
 * This facade provides a static factory method for license management context builders for use with Version 4 (V4)
 * format license keys.
 * This class should not be used by applications because the created license management context builders are only
 * partially configured.
 */
public final class V4 {

    @Obfuscate
    private static final String ENCRYPTION_ALGORITHM = "PBEWithHmacSHA256AndAES_128";

    @Obfuscate
    private static final String KEYSTORE_TYPE = "PKCS12";

    private static final V4Context context = new V4Context();

    /**
     * Returns a new license management context builder for managing V4 format license keys.
     */
    public static LicenseManagementContextBuilder builder() {
        return builder(context);
    }

    /**
     * Returns a new license management context builder for managing V4 format license keys using the given context.
     */
    @SuppressWarnings("WeakerAccess")
    public static LicenseManagementContextBuilder builder(V4Context context) {
        return Core
                .builder()
                .codec(context.codec())
                .compression(deflate(Deflater.BEST_COMPRESSION))
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V4Encryption::new)
                .licenseFactory(new V4LicenseFactory())
                .repositoryFactory(repositoryFactory())
                .keystoreType(KEYSTORE_TYPE);
    }

    /**
     * For testing only: Returns the repository factory for use with V4 format license keys.
     */
    public static RepositoryFactory<?> repositoryFactory() {
        return new V4RepositoryFactory();
    }

    private V4() {
    }
}

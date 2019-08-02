/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v1;

import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.core.Core;
import global.namespace.truelicense.obfuscate.Obfuscate;

import static global.namespace.fun.io.bios.BIOS.gzip;

/**
 * This facade provides a static factory method for license management context builders for Version 1 (V1) format
 * license keys.
 * This class is provided to enable applications to generate, install, verify and uninstall license keys in the format
 * for TrueLicense 1.X applications.
 *
 * @deprecated Since TrueLicense 2, this format is deprecated and should not be used for new applications.
 */
@Deprecated
public final class V1 {

    @Obfuscate
    private static final String ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";

    @Obfuscate
    private static final String KEYSTORE_TYPE = "JKS";

    private static final V1Context context = new V1Context();

    /**
     * Returns a new license management context builder for managing Version 1 (V1) format license keys using the given
     * context.
     */
    public static LicenseManagementContextBuilder builder() {
        return builder(context);
    }

    /**
     * Returns a new license management context builder for managing Version 1 (V1) format license keys.
     */
    @SuppressWarnings("WeakerAccess")
    public static LicenseManagementContextBuilder builder(V1Context context) {
        return Core
                .builder()
                .codec(context.codec())
                .compression(gzip())
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V1Encryption::new)
                .licenseFactory(new V1LicenseFactory())
                .repositoryFactory(repositoryFactory())
                .keystoreType(KEYSTORE_TYPE);
    }

    /**
     * For testing only: Returns the repository factory for use with V1 format license keys.
     */
    public static RepositoryFactory<?> repositoryFactory() {
        return new V1RepositoryFactory();
    }

    private V1() {
    }
}

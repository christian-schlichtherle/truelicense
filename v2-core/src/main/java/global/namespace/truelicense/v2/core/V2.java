/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.core;

import global.namespace.truelicense.api.LicenseManagementContextBuilder;
import global.namespace.truelicense.core.Core;
import global.namespace.truelicense.obfuscate.Obfuscate;
import global.namespace.truelicense.v2.core.auth.V2RepositoryFactory;

import java.util.zip.Deflater;

import static global.namespace.fun.io.bios.BIOS.deflate;

/**
 * This facade provides a static factory method for license management context builders for Version 2 (V2) format
 * license keys.
 * This class should not be used by applications because the created license management context builders are only
 * partially configured.
 *
 * @deprecated Since TrueLicense 4, this format is deprecated and should not be used for new applications.
 */
@Deprecated
public final class V2 {

    @Obfuscate
    private static final String ENCRYPTION_ALGORITHM = "PBEWithSHA1AndDESede";

    @Obfuscate
    private static final String KEYSTORE_TYPE = "JCEKS";

    /**
     * Returns a new license management context builder for use with Version 2 (V2) format license keys.
     * This method should not be called by applications because the returned license management context builder is only
     * partially configured.
     */
    public static LicenseManagementContextBuilder builder() {
        return Core
                .builder()
                .compression(deflate(Deflater.BEST_COMPRESSION))
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V2Encryption::new)
                .licenseFactory(new V2LicenseFactory())
                .repositoryFactory(new V2RepositoryFactory())
                .keystoreType(KEYSTORE_TYPE);
    }

    private V2() { }

}

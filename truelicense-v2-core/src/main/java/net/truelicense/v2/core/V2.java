/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.v2.core;

import net.truelicense.api.License;
import net.truelicense.api.LicenseFactory;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.core.Core;
import net.truelicense.obfuscate.Obfuscate;
import net.truelicense.v2.core.auth.V2RepositoryContext;

import java.util.zip.Deflater;

import static global.namespace.fun.io.bios.BIOS.deflate;

/**
 * This facade provides a static factory method for license management context builders for Version 2 (V2) format
 * license keys.
 * This class should not be used by applications because the created license management context builders are only
 * partially configured.
 *
 * @author Christian Schlichtherle
 */
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
        final LicenseFactory licenseFactory = new LicenseFactory() {

            @Override
            public License license() { return new License(); }

            @Override
            public Class<? extends License> licenseClass() { return License.class; }
        };
        return Core
                .builder()
                .compression(deflate(Deflater.BEST_COMPRESSION))
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V2Encryption::new)
                .licenseFactory(licenseFactory)
                .repositoryContext(new V2RepositoryContext())
                .keystoreType(KEYSTORE_TYPE);
    }

    private V2() { }
}

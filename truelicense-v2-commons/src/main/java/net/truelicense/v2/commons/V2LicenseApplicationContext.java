/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.commons;

import net.truelicense.api.License;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.core.TrueLicenseApplicationContext;
import net.truelicense.obfuscate.Obfuscate;
import net.truelicense.v2.commons.auth.V2RepositoryContext;
import net.truelicense.v2.commons.crypto.V2Encryption;

import java.util.zip.Deflater;

import static global.namespace.fun.io.bios.BIOS.deflate;

/**
 * The root context for applications which need to manage V2/* format license keys.
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public abstract class V2LicenseApplicationContext extends TrueLicenseApplicationContext {

    @Obfuscate
    private static final String ENCRYPTION_ALGORITHM = "PBEWithSHA1AndDESede";

    @Obfuscate
    private static final String KEYSTORE_TYPE = "JCEKS";

    @Override
    public LicenseManagementContextBuilder context() {
        return super.context()
                .compression(deflate(Deflater.BEST_COMPRESSION))
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V2Encryption::new)
                .licenseFactory(License::new)
                .repositoryContext(new V2RepositoryContext())
                .keystoreType(KEYSTORE_TYPE);
    }
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v1;

import de.schlichtherle.license.LicenseContent;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.core.TrueLicenseApplicationContext;
import net.truelicense.obfuscate.Obfuscate;
import net.truelicense.v1.auth.V1RepositoryContext;
import net.truelicense.v1.codec.X500PrincipalXmlCodec;
import net.truelicense.v1.crypto.V1Encryption;

import static global.namespace.fun.io.bios.BIOS.gzip;

/**
 * The root context for applications which need to manage Version 1 (V1) format license keys.
 * This class is provided to enable applications to generate, install, verify and uninstall license keys in the format
 * for TrueLicense 1.X applications.
 * Since TrueLicense 2.0, this format is obsolete and should not be used for new applications!
 * <p>
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class V1LicenseApplicationContext extends TrueLicenseApplicationContext {

    @Obfuscate
    private static final String ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";

    @Obfuscate
    private static final String KEYSTORE_TYPE = "JKS";

    @Override
    public LicenseManagementContextBuilder context() {
        return super.context()
                .codec(new X500PrincipalXmlCodec())
                .compression(gzip())
                .encryptionAlgorithm(ENCRYPTION_ALGORITHM)
                .encryptionFactory(V1Encryption::new)
                .licenseFactory(LicenseContent::new)
                .repositoryContext(new V1RepositoryContext())
                .keystoreType(KEYSTORE_TYPE);
    }
}

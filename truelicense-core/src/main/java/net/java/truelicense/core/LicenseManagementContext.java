/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import net.java.truelicense.core.auth.Authentication;
import net.java.truelicense.core.auth.KeyStoreParameters;
import net.java.truelicense.core.auth.RepositoryProvider;
import net.java.truelicense.core.codec.CodecProvider;
import net.java.truelicense.core.comp.CompressionProvider;
import net.java.truelicense.core.crypto.Encryption;
import net.java.truelicense.core.crypto.PbeParameters;
import net.java.truelicense.core.policy.PasswordPolicyProvider;
import net.java.truelicense.core.util.*;

/**
 * A root context for the life cycle management of license keys.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContext
extends CachePeriodProvider,
        ClassLoaderProvider,
        Clock,
        CodecProvider,
        CompressionProvider,
        LicenseAuthorizationProvider,
        LicenseInitializationProvider,
        LicenseProvider,
        LicenseSubjectProvider,
        LicenseValidationProvider,
        PasswordPolicyProvider,
        RepositoryProvider {

    /** Returns a <em>new</em> license. */
    @Override License license();

    /**
     * Returns the name of the default key store type,
     * for example {@code "JCEKS"} or {@code "JKS"}.
     * You can override this default value when configuring the key store based
     * authentication with the license vendor context or the license consumer
     * context.
     *
     * @see   LicenseVendorContext#keyStore
     * @see   LicenseConsumerContext#keyStore
     * @see   LicenseConsumerContext#ftpKeyStore
     * @see   LicenseApplicationContext.KsbaInjection#storeType
     * @since TrueLicense 2.1
     */
    String storeType();

    /**
     * Returns an authentication for the given key store parameters.
     *
     * @param parameters the key store parameters.
     */
    Authentication authentication(KeyStoreParameters parameters);

    /**
     * Returns the name of the default Password Based Encryption (PBE)
     * algorithm for the license key format.
     * You can override this default value when configuring the PBE with the
     * license vendor context or the license consumer context.
     *
     * @see   LicenseConsumerContext#pbe
     * @see   LicenseVendorContext#pbe
     * @see   LicenseApplicationContext.PbeInjection#algorithm
     * @since TrueLicense 2.1
     */
    String pbeAlgorithm();

    /**
     * Returns an encryption for the given PBE parameters.
     *
     * @param parameters the PBE parameters.
     */
    Encryption encryption(PbeParameters parameters);

    /**
     * Returns a context for license vendor applications alias license key
     * tools.
     */
    LicenseVendorContext vendor();

    /**
     * Returns a context for license consumer applications.
     */
    LicenseConsumerContext consumer();
}

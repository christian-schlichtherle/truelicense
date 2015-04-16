/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.KeyStoreParameters;
import org.truelicense.api.auth.RepositoryProvider;
import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.comp.CompressionProvider;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.BiosProvider;
import org.truelicense.api.misc.CachePeriodProvider;
import org.truelicense.api.misc.ClassLoaderProvider;
import org.truelicense.api.misc.Clock;
import org.truelicense.api.passwd.PasswordPolicyProvider;
import org.truelicense.api.passwd.PasswordProtectionProvider;

/**
 * A root context for the life cycle management of license keys.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContext<PasswordSpecification>
extends BiosProvider,
        CachePeriodProvider,
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
        PasswordProtectionProvider<PasswordSpecification>,
        RepositoryProvider {

    /**
     * Returns an authentication for the given key store parameters.
     *
     * @param parameters the key store parameters.
     */
    Authentication authentication(KeyStoreParameters parameters);

    /**
     * Returns a context for license consumer applications.
     */
    LicenseConsumerContext<PasswordSpecification> consumer();

    /**
     * Returns an encryption for the given PBE parameters.
     *
     * @param parameters the PBE parameters.
     */
    Encryption encryption(PbeParameters parameters);

    /** Returns a <em>new</em> license. */
    @Override
    License license();

    /**
     * Returns the name of the default Password Based Encryption (PBE)
     * algorithm for the license key format.
     * You can override this default value when configuring the PBE with the
     * license vendor context or the license consumer context.
     *
     * @see   LicenseApplicationContext.PbeInjection#algorithm
     */
    String pbeAlgorithm();

    /**
     * Returns the name of the default key store type,
     * for example {@code "JCEKS"} or {@code "JKS"}.
     * You can override this default value when configuring the key store based
     * authentication with the license vendor context or the license consumer
     * context.
     */
    String storeType();

    /**
     * Returns a context for license vendor applications alias license key
     * generators.
     */
    LicenseVendorContext<PasswordSpecification> vendor();
}

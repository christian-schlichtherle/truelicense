/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.auth.AuthenticationFunction;
import net.truelicense.api.crypto.EncryptionFunction;
import net.truelicense.api.misc.Builder;
import net.truelicense.api.misc.Clock;
import net.truelicense.api.passwd.PasswordPolicy;

/**
 * A builder for
 * {@linkplain LicenseManagementContext license management contexts}.
 * Call its {@link #build} method to obtain the configured license management
 * context.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContextBuilder
extends Builder<LicenseManagementContext> {

    /**
     * Sets the authentication function (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder authenticationFunction(AuthenticationFunction function);

    /**
     * Sets the license management authorization (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder authorization(LicenseManagementAuthorization authorization);

    /**
     * Sets the cache period for external changes to the license key in milliseconds (optional).
     * Any non-negative value is valid.
     * Pass {@link Long#MAX_VALUE} to disable the timeout or zero to disable the caching of intermediate results.
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder cachePeriodMillis(long cachePeriodMillis);

    /**
     * Sets the clock (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder clock(Clock clock);

    /**
     * Sets the (password based) encryption function.
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder encryptionFunction(EncryptionFunction function);

    /**
     * Sets the custom license initialization (optional).
     *
     * @see #initializationComposition(LicenseFunctionComposition)
     * @return {@code this}
     */
    LicenseManagementContextBuilder initialization(LicenseInitialization initialization);

    /**
     * Sets the composition of the custom license initialization and the
     * built-in license initialization (optional).
     * This property is ignored if no custom license initialization is set.
     * Otherwise, if this method is not called, then
     * {@link LicenseFunctionComposition#decorate} is used.
     *
     * @see #initialization(LicenseInitialization)
     * @return {@code this}
     */
    LicenseManagementContextBuilder initializationComposition(LicenseFunctionComposition composition);

    /**
     * Sets the password policy (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder passwordPolicy(PasswordPolicy policy);

    /**
     * Sets the license subject.
     * The provided string should get computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * {@code @net.truelicense.obfuscate.Obfuscate} annotation and processing it
     * with the TrueLicense Maven Plugin.
     *
     * @param subject the license subject, i.e. a product name with an optional
     *                version range, e.g. {@code MyApp 1}.
     * @return {@code this}
     */
    LicenseManagementContextBuilder subject(String subject);

    /**
     * Sets the custom license validation (optional).
     *
     * @see #validationComposition(LicenseFunctionComposition)
     * @return {@code this}
     */
    LicenseManagementContextBuilder validation(LicenseValidation validation);

    /**
     * Sets the composition of the custom license validation and the built-in
     * license validation (optional).
     * This property is ignored if no custom license validation is set.
     * Otherwise, if this method is not called, then
     * {@link LicenseFunctionComposition#decorate} is used.
     *
     * @see #validation(LicenseValidation)
     * @return {@code this}
     */
    LicenseManagementContextBuilder validationComposition(LicenseFunctionComposition composition);
}

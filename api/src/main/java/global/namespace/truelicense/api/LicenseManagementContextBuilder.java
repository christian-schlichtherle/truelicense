/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.fun.io.api.Filter;
import global.namespace.truelicense.api.auth.AuthenticationChildBuilder;
import global.namespace.truelicense.api.auth.AuthenticationFactory;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.builder.GenBuilder;
import global.namespace.truelicense.api.codec.CodecFactory;
import global.namespace.truelicense.api.crypto.EncryptionChildBuilder;
import global.namespace.truelicense.api.crypto.EncryptionFactory;
import global.namespace.truelicense.api.passwd.PasswordPolicy;

import java.time.Clock;

/**
 * A builder for
 * {@linkplain LicenseManagementContext license management contexts}.
 * Call its {@link #build} method to obtain the configured license management
 * context.
 */
public interface LicenseManagementContextBuilder
extends GenBuilder<LicenseManagementContext> {

    /**
     * Sets the authentication factory (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder authenticationFactory(AuthenticationFactory authenticationFactory);

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
     * If this method is not called, then the system clock is used.
     * However, in order to protect against date/time forgery, a more sophisticated implementation should use an
     * authoritative time source, e.g. a radio clock, an Internet time server or extrapolate an approximation from some
     * timestamps in input data.
     * If resolving the current date/time from an authoritative resource fails for any reason, the implementation should
     * simply fallback to use the system clock.
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder clock(Clock clock);

    /**
     * Sets the codec (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder codecFactory(CodecFactory codecFactory);

    /**
     * Sets the compression transformation (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder compression(Filter compression);

    /**
     * Sets the default name of the password based encryption algorithm (optional).
     * You can override this default value when configuring the password based encryption.
     *
     * @see EncryptionChildBuilder#algorithm(String)
     * @return {@code this}
     */
    LicenseManagementContextBuilder encryptionAlgorithm(String encryptionAlgorithm);

    /**
     * Sets the (password based) encryption factory (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder encryptionFactory(EncryptionFactory encryptionFactory);

    /**
     * Sets the custom license initialization (optional).
     *
     * @see #initializationComposition(LicenseFunctionComposition)
     * @return {@code this}
     */
    LicenseManagementContextBuilder initialization(LicenseInitialization initialization);

    /**
     * Sets the composition of the custom license initialization and the built-in license initialization (optional).
     * This property is ignored if no custom license initialization is set.
     * Otherwise, if this method is not called, then {@link LicenseFunctionComposition#decorate} is used.
     *
     * @see #initialization(LicenseInitialization)
     * @return {@code this}
     */
    LicenseManagementContextBuilder initializationComposition(LicenseFunctionComposition composition);

    /**
     * Sets the license factory (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder licenseFactory(LicenseFactory licenseFactory);

    /**
     * Sets the password policy (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder passwordPolicy(PasswordPolicy policy);

    /**
     * Sets the repository factory (optional).
     *
     * @return {@code this}
     */
    LicenseManagementContextBuilder repositoryFactory(RepositoryFactory<?> repositoryFactory);

    /**
     * Sets the default type of the keystore (optional).
     * You can override this default value when configuring the keystore based authentication.
     *
     * @see AuthenticationChildBuilder#storeType(String)
     * @return {@code this}
     */
    LicenseManagementContextBuilder keystoreType(String keystoreType);

    /**
     * Sets the license subject.
     * The provided string should get computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * {@code @global.namespace.truelicense.obfuscate.Obfuscate} annotation and processing it
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
     * Sets the composition of the custom license validation and the built-in license validation (optional).
     * This property is ignored if no custom license validation is set.
     * Otherwise, if this method is not called, then {@link LicenseFunctionComposition#decorate} is used.
     *
     * @see #validation(LicenseValidation)
     * @return {@code this}
     */
    LicenseManagementContextBuilder validationComposition(LicenseFunctionComposition composition);
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.KeyStoreParameters;
import org.truelicense.api.auth.RepositoryContextProvider;
import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.comp.CompressionProvider;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.io.Transformation;
import org.truelicense.api.misc.*;
import org.truelicense.api.passwd.PasswordPolicy;
import org.truelicense.api.passwd.PasswordPolicyProvider;
import org.truelicense.api.passwd.PasswordProtection;
import org.truelicense.api.passwd.PasswordProtectionProvider;
import org.truelicense.core.auth.Notary;
import org.truelicense.core.passwd.Passwords;
import org.truelicense.obfuscate.ObfuscatedString;
import org.truelicense.spi.io.BIOS;
import org.truelicense.spi.io.BiosProvider;
import org.truelicense.spi.io.StandardBIOS;
import org.truelicense.spi.misc.Option;

import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A basic license management context.
 * This class is immutable.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @param <Model> the type of the repository model.
 * @author Christian Schlichtherle
 */
public abstract class TrueLicenseApplicationContext<Model>
implements BiosProvider,
        CachePeriodProvider,
        ClassLoaderProvider,
        Clock,
        CodecProvider,
        CompressionProvider,
        LicenseApplicationContext,
        LicenseManagementAuthorizationProvider,
        LicenseFactory,
        PasswordPolicyProvider,
        PasswordProtectionProvider<ObfuscatedString>,
        RepositoryContextProvider<Model> {

    /**
     * Returns an authentication for the given key store parameters.
     * <p>
     * The implementation in the class {@link TrueLicenseManagementContext}
     * returns a new {@link Notary} for the given key store parameters.
     *
     * @param parameters the key store parameters.
     */
    public Authentication authentication(KeyStoreParameters parameters) {
        return new Notary(parameters);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns an authorization which clears all operation requests.
     */
    @Override
    public final LicenseManagementAuthorization authorization() {
        return new TrueLicenseManagementAuthorization();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a {@link StandardBIOS}.
     */
    @Override
    public BIOS bios() { return new StandardBIOS(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns half an hour (in milliseconds) to account for external changes
     * to the configured store for the license key.
     */
    @Override
    public long cachePeriodMillis() { return 30 * 60 * 1000; }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * lists the current thread's context class loader, if not {@code null}.
     */
    @Override
    public final List<ClassLoader> classLoader() {
        return Option.wrap(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public final TrueLicenseManagementContextBuilder context() {
        return new TrueLicenseManagementContextBuilder();
    }

    /**
     * Returns an encryption for the given PBE parameters.
     *
     * @param parameters the PBE parameters.
     */
    public abstract Transformation encryption(PbeParameters parameters);

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns a new {@link Date}.
     */
    @Override
    public final Date now() {
        return new Date();
    }

    /**
     * Returns the name of the default Password Based Encryption (PBE)
     * algorithm for the license key format.
     * You can override this default value when configuring the PBE.
     *
     * @see PbeInjection#algorithm
     */
    public abstract String pbeAlgorithm();

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns {@link Passwords#newPasswordPolicy()}.
     */
    @Override
    public PasswordPolicy policy() { return Passwords.newPasswordPolicy(); }

    /**
     * Returns a password protection for the given representation of an
     * obfuscated string.
     * Calling this method creates a new {@link ObfuscatedString} from the given
     * array and forwards the call to {@link #protection(ObfuscatedString)}.
     */
    public final PasswordProtection protection(long[] obfuscated) {
        return protection(new ObfuscatedString(obfuscated));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link TrueLicenseApplicationContext}
     * returns
     * {@link Passwords#newPasswordProtection(ObfuscatedString) Passwords.newPasswordProtecetion(os)}.
     */
    @Override
    public final PasswordProtection protection(ObfuscatedString os) {
        return Passwords.newPasswordProtection(os);
    }

    /**
     * Returns the name of the default key store type,
     * for example {@code "JCEKS"} or {@code "JKS"}.
     * You can override this default value when configuring the key store based
     * authentication.
     */
    public abstract String storeType();

    public final class TrueLicenseManagementContextBuilder
    implements ContextProvider<TrueLicenseApplicationContext>,
               LicenseManagementContextBuilder {

        LicenseManagementAuthorization authorization = context().authorization();
        Clock clock = context();
        List<LicenseInitialization> initialization = Option.none();
        LicenseFunctionComposition initializationComposition = LicenseFunctionComposition.decorate;
        String subject;
        List<LicenseValidation> validation = Option.none();
        LicenseFunctionComposition validationComposition = LicenseFunctionComposition.decorate;

        @Override
        public TrueLicenseManagementContextBuilder authorization(final LicenseManagementAuthorization authorization) {
            this.authorization = requireNonNull(authorization);
            return this;
        }

        @Override
        public TrueLicenseManagementContextBuilder clock(final Clock clock) {
            this.clock = requireNonNull(clock);
            return this;
        }

        @Override
        public TrueLicenseApplicationContext<Model> context() {
            return TrueLicenseApplicationContext.this;
        }

        @Override
        public TrueLicenseManagementContextBuilder initialization(final LicenseInitialization initialization) {
            this.initialization = Option.wrap(initialization);
            return this;
        }

        @Override
        public TrueLicenseManagementContextBuilder initializationComposition(final LicenseFunctionComposition composition) {
            this.initializationComposition = requireNonNull(composition);
            return this;
        }

        @Override
        public TrueLicenseManagementContextBuilder subject(final String subject) {
            this.subject = requireNonNull(subject);
            return this;
        }

        @Override
        public TrueLicenseManagementContextBuilder validation(final LicenseValidation validation) {
            this.validation = Option.wrap(validation);
            return this;
        }

        @Override
        public TrueLicenseManagementContextBuilder validationComposition(final LicenseFunctionComposition composition) {
            this.validationComposition = requireNonNull(composition);
            return this;
        }

        @Override
        public TrueLicenseManagementContext<Model> build() {
            return new TrueLicenseManagementContext<>(this);
        }
    }
}

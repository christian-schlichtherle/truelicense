/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.core;

import global.namespace.fun.io.api.Filter;
import net.truelicense.api.*;
import net.truelicense.api.auth.AuthenticationFactory;
import net.truelicense.api.auth.RepositoryContext;
import net.truelicense.api.codec.Codec;
import net.truelicense.api.crypto.EncryptionFactory;
import net.truelicense.api.passwd.PasswordPolicy;
import net.truelicense.core.auth.Notary;
import net.truelicense.core.misc.Strings;
import net.truelicense.core.passwd.MinimumPasswordPolicy;

import java.time.Clock;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/** @author Christian Schlichtherle */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
final class TrueLicenseManagementContextBuilder implements LicenseManagementContextBuilder {

    AuthenticationFactory authenticationFactory = Notary::new;
    LicenseManagementAuthorization authorization = new TrueLicenseManagementAuthorization();
    long cachePeriodMillis = 30 * 60 * 1000;
    Clock clock = Clock.systemDefaultZone();
    Optional<Codec> codec = Optional.empty();
    Optional<Filter> compression = Optional.empty();
    String encryptionAlgorithm = "";
    Optional<EncryptionFactory> encryptionFactory = Optional.empty();
    Optional<LicenseFactory> licenseFactory = Optional.empty();
    Optional<LicenseInitialization> initialization = Optional.empty();
    LicenseFunctionComposition initializationComposition = LicenseFunctionComposition.decorate;
    PasswordPolicy passwordPolicy = new MinimumPasswordPolicy();
    Optional<RepositoryContext<?>> repositoryContext = Optional.empty();
    String subject = "";
    String keystoreType = "";
    Optional<LicenseValidation> validation = Optional.empty();
    LicenseFunctionComposition validationComposition = LicenseFunctionComposition.decorate;

    @Override
    public LicenseManagementContextBuilder authenticationFactory(final AuthenticationFactory authenticationFactory) {
        this.authenticationFactory = requireNonNull(authenticationFactory);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder authorization(final LicenseManagementAuthorization authorization) {
        this.authorization = requireNonNull(authorization);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder cachePeriodMillis(final long cachePeriodMillis) {
        if (cachePeriodMillis < 0) {
            throw new IllegalArgumentException("" + cachePeriodMillis);
        }
        this.cachePeriodMillis = cachePeriodMillis;
        return this;
    }

    @Override
    public LicenseManagementContextBuilder clock(final Clock clock) {
        this.clock = requireNonNull(clock);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder codec(final Codec codec) {
        this.codec = Optional.of(codec);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder compression(final Filter compression) {
        this.compression = Optional.of(compression);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder encryptionAlgorithm(final String encryptionAlgorithm) {
        this.encryptionAlgorithm = Strings.requireNonEmpty(encryptionAlgorithm);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder encryptionFactory(final EncryptionFactory encryptionFactory) {
        this.encryptionFactory = Optional.of(encryptionFactory);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder initialization(final LicenseInitialization initialization) {
        this.initialization = Optional.ofNullable(initialization);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder initializationComposition(final LicenseFunctionComposition composition) {
        this.initializationComposition = requireNonNull(composition);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder licenseFactory(final LicenseFactory licenseFactory) {
        this.licenseFactory = Optional.of(licenseFactory);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder passwordPolicy(final PasswordPolicy passwordPolicy) {
        this.passwordPolicy = requireNonNull(passwordPolicy);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder repositoryContext(final RepositoryContext<?> repositoryContext) {
        this.repositoryContext = Optional.of(repositoryContext);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder keystoreType(final String keystoreType) {
        this.keystoreType = Strings.requireNonEmpty(keystoreType);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder subject(final String subject) {
        this.subject = Strings.requireNonEmpty(subject);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder validation(final LicenseValidation validation) {
        this.validation = Optional.ofNullable(validation);
        return this;
    }

    @Override
    public LicenseManagementContextBuilder validationComposition(final LicenseFunctionComposition composition) {
        this.validationComposition = requireNonNull(composition);
        return this;
    }

    @Override
    public LicenseManagementContext build() { return new TrueLicenseManagementContext(this); }
}

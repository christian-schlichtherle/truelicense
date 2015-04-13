/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.License;
import org.truelicense.api.LicenseParameters;
import org.truelicense.api.LicenseVendorContext;
import org.truelicense.api.LicenseVendorManager;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * A basic context for license vendor applications alias license key tools.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
@Immutable
final class BasicLicenseVendorContext<PasswordSpecification>
extends BasicLicenseApplicationContext<PasswordSpecification>
implements LicenseVendorContext<PasswordSpecification> {

    BasicLicenseVendorContext(BasicLicenseManagementContext<PasswordSpecification> context) {
        super(context);
    }

    @Override public License license() { return context().license(); }

    @Override public final Codec codec() { return context().codec(); }

    LicenseVendorManager manager(
            Authentication authentication,
            Encryption encryption) {
        return manager(parameters(authentication, encryption));
    }

    private LicenseVendorManager manager(final LicenseParameters lp) {
        assert null != lp;
        class Manager extends BasicLicenseManager implements LicenseVendorManager {

            final BasicLicenseVendorContext vc = BasicLicenseVendorContext.this;
            final String subject = vc.subject();

            @Override public String subject() { return subject; }

            @Override public LicenseVendorContext context() { return vc; }

            @Override public Store store() {
                throw new UnsupportedOperationException();
            }
            @Override public LicenseParameters parameters() { return lp; }
        }
        return new Manager();
    }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder<PasswordSpecification> manager() {
        @ParametersAreNullableByDefault
        class ManagerConfiguration implements ManagerBuilder<PasswordSpecification> {

            final BasicLicenseVendorContext<PasswordSpecification> vc = BasicLicenseVendorContext.this;

            @Nullable Authentication authentication;
            @Nullable Encryption encryption;

            @Override
            public LicenseVendorManager build() {
                return vc.manager(authentication, encryption);
            }

            @Override
            public ManagerBuilder<PasswordSpecification> authentication(final Authentication authentication) {
                this.authentication = authentication;
                return this;
            }

            @Override
            public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyStore() {
                @ParametersAreNullableByDefault
                class KeyStoreConfiguration implements KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {
                    @Nullable String storeType, alias;
                    @Nullable Source source;
                    @Nullable PasswordSpecification storePassword, keyPassword;

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return authentication(
                                vc.keyStore(source, storeType, storePassword, alias, keyPassword));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storeType(final String storeType) {
                        this.storeType = storeType;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFrom(final Source source) {
                        this.source = source;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFromResource(String name) {
                        return loadFrom(vc.resource(name));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storePassword(final PasswordSpecification storePassword) {
                        this.storePassword = storePassword;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> alias(final String alias) {
                        this.alias = alias;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyPassword(final PasswordSpecification keyPassword) {
                        this.keyPassword = keyPassword;
                        return this;
                    }
                }
                return new KeyStoreConfiguration();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> encryption(final Encryption encryption) {
                this.encryption = encryption;
                return this;
            }

            @Override
            public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> encryption() {
                @ParametersAreNullableByDefault
                class EncryptionConfiguration implements PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {
                    @Nullable String algorithm;
                    @Nullable PasswordSpecification password;

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return encryption(vc.pbe(algorithm, password));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> algorithm(final String algorithm) {
                        this.algorithm = algorithm;
                        return this;
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> password(final PasswordSpecification password) {
                        this.password = password;
                        return this;
                    }
                }
                return new EncryptionConfiguration();
            }
        }
        return new ManagerConfiguration();
    }
}

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
import org.truelicense.api.passwd.PasswordProtection;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
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
 * @author Christian Schlichtherle
 */
@Immutable
final class BasicLicenseVendorContext extends BasicLicenseApplicationContext
implements LicenseVendorContext {

    BasicLicenseVendorContext(BasicLicenseManagementContext context) {
        super(context);
    }

    @Override public License license() { return context().license(); }

    @Override public final Codec codec() { return context().codec(); }

    Authentication keyStore(
            @CheckForNull Source source,
            @CheckForNull String storeType,
            PasswordProtection storeProtection,
            String alias,
            @CheckForNull PasswordProtection keyProtection) {
        return context().authentication(apChecked(
                source, storeType, storeProtection, alias, keyProtection));
    }

    Encryption pbe(
            @CheckForNull String algorithm,
            PasswordProtection protection) {
        return context().encryption(pbeChecked(algorithm, protection));
    }

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
    @Override public ManagerBuilder manager() {
        return new ManagerBuilder() {
            final BasicLicenseVendorContext vc = BasicLicenseVendorContext.this;

            @Nullable Authentication authentication;
            @Nullable Encryption encryption;

            @Override
            public LicenseVendorManager build() {
                return vc.manager(authentication, encryption);
            }

            @Override
            public ManagerBuilder authentication(final Authentication authentication) {
                this.authentication = authentication;
                return this;
            }

            @Override
            public KsbaInjection<ManagerBuilder> keyStore() {
                return new KsbaInjection<ManagerBuilder>() {
                    @Nullable String storeType, alias;
                    @Nullable Source source;
                    @Nullable PasswordProtection storeProtection, keyProtection;

                    @Override
                    public ManagerBuilder inject() {
                        return authentication(
                                vc.keyStore(source, storeType, storeProtection, alias, keyProtection));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> storeType(
                            final @CheckForNull String storeType) {
                        this.storeType = storeType;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> loadFrom(final Source source) {
                        this.source = source;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> loadFromResource(String name) {
                        return loadFrom(vc.resource(name));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> storeProtection(final PasswordProtection storeProtection) {
                        this.storeProtection = storeProtection;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> alias(final String alias) {
                        this.alias = alias;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> keyProtection(final PasswordProtection keyProtection) {
                        this.keyProtection = keyProtection;
                        return this;
                    }
                };
            }

            @Override
            public ManagerBuilder encryption(final Encryption encryption) {
                this.encryption = encryption;
                return this;
            }

            @Override
            public PbeInjection<ManagerBuilder> encryption() {
                return new PbeInjection<ManagerBuilder>() {
                    @Nullable String algorithm;
                    @Nullable PasswordProtection protection;

                    @Override
                    public ManagerBuilder inject() {
                        return encryption(vc.pbe(algorithm, protection));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder> algorithm(
                            final @CheckForNull String algorithm) {
                        this.algorithm = algorithm;
                        return this;
                    }

                    @Override
                    public PbeInjection<ManagerBuilder> protection(final PasswordProtection protection) {
                        this.protection = protection;
                        return this;
                    }
                };
            }
        };
    }
}

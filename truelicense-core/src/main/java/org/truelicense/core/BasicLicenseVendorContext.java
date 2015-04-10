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
import org.truelicense.obfuscate.ObfuscatedString;

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
final class BasicLicenseVendorContext
extends BasicLicenseApplicationContext
implements LicenseVendorContext<ObfuscatedString> {

    BasicLicenseVendorContext(BasicLicenseManagementContext context) {
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
    @Override public ManagerBuilder<ObfuscatedString> manager() {
        return new ManagerBuilder<ObfuscatedString>() {

            final BasicLicenseVendorContext vc = BasicLicenseVendorContext.this;

            @Nullable Authentication authentication;
            @Nullable Encryption encryption;

            @Override
            public LicenseVendorManager build() {
                return vc.manager(authentication, encryption);
            }

            @Override
            public ManagerBuilder<ObfuscatedString> authentication(final Authentication authentication) {
                this.authentication = authentication;
                return this;
            }

            @Override
            public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> keyStore() {
                return new KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString>() {
                    @Nullable String storeType, alias;
                    @Nullable Source source;
                    @Nullable ObfuscatedString storePassword, keyPassword;

                    @Override
                    public ManagerBuilder<ObfuscatedString> inject() {
                        return authentication(
                                vc.keyStore(source, storeType, storePassword, alias, keyPassword));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> storeType(
                            final @CheckForNull String storeType) {
                        this.storeType = storeType;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> loadFrom(final Source source) {
                        this.source = source;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> loadFromResource(String name) {
                        return loadFrom(vc.resource(name));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> storePassword(final ObfuscatedString storePassword) {
                        this.storePassword = storePassword;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> alias(final String alias) {
                        this.alias = alias;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> keyPassword(final ObfuscatedString keyPassword) {
                        this.keyPassword = keyPassword;
                        return this;
                    }
                };
            }

            @Override
            public ManagerBuilder<ObfuscatedString> encryption(final Encryption encryption) {
                this.encryption = encryption;
                return this;
            }

            @Override
            public PbeInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> encryption() {
                return new PbeInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString>() {
                    @Nullable String algorithm;
                    @Nullable ObfuscatedString password;

                    @Override
                    public ManagerBuilder<ObfuscatedString> inject() {
                        return encryption(vc.pbe(algorithm, password));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> algorithm(
                            final @CheckForNull String algorithm) {
                        this.algorithm = algorithm;
                        return this;
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> password(final ObfuscatedString password) {
                        this.password = password;
                        return this;
                    }
                };
            }
        };
    }
}

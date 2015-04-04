/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import net.java.truelicense.core.auth.Authentication;
import net.java.truelicense.core.codec.Codec;
import net.java.truelicense.core.crypto.Encryption;
import net.java.truelicense.core.io.Source;
import net.java.truelicense.core.io.Store;
import net.java.truelicense.obfuscate.ObfuscatedString;

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

    @Override public Authentication keyStore(
            @CheckForNull Source source,
            @CheckForNull String storeType,
            ObfuscatedString storePassword,
            String alias,
            @CheckForNull ObfuscatedString keyPassword) {
        return context().authentication(kspChecked(
                source, storeType, storePassword, alias, keyPassword));
    }

    @Override public Encryption pbe(
            @CheckForNull String algorithm,
            ObfuscatedString password) {
        return context().encryption(pbeChecked(algorithm, password));
    }

    @Override public LicenseVendorManager manager(
            Authentication authentication,
            Encryption encryption) {
        return manager(parameters(authentication, encryption));
    }

    private LicenseVendorManager manager(final LicenseParameters lp) {
        assert null != lp;
        return new Manager() {
            @Override public LicenseParameters parameters() { return lp; }
        };
    }

    private abstract class Manager extends BasicLicenseManager
    implements LicenseVendorManager {

        final String subject = BasicLicenseVendorContext.this.subject();

        @Override public String subject() { return subject; }

        @Override public LicenseVendorContext context() {
            return BasicLicenseVendorContext.this;
        }

        @Override public Store store() {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder manager() {
        return new ManagerBuilder() {
            final LicenseVendorContext vc = BasicLicenseVendorContext.this;

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
                    @Nullable ObfuscatedString storePassword, keyPassword;

                    @Override
                    public ManagerBuilder inject() {
                        return authentication(
                                vc.keyStore(source, storeType, storePassword, alias, keyPassword));
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
                    public KsbaInjection<ManagerBuilder> storePassword(final ObfuscatedString storePassword) {
                        this.storePassword = storePassword;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> alias(final String alias) {
                        this.alias = alias;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder> keyPassword(final ObfuscatedString keyPassword) {
                        this.keyPassword = keyPassword;
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
            public PbeInjection<ManagerBuilder> pbe() {
                return new PbeInjection<ManagerBuilder>() {
                    @Nullable String algorithm;
                    @Nullable ObfuscatedString password;

                    @Override
                    public ManagerBuilder inject() {
                        return encryption(vc.pbe(algorithm, password));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder> algorithm(
                            final @CheckForNull String algorithm) {
                        this.algorithm = algorithm;
                        return this;
                    }

                    @Override
                    public PbeInjection<ManagerBuilder> password(final ObfuscatedString password) {
                        this.password = password;
                        return this;
                    }
                };
            }
        };
    }
}

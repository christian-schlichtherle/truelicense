/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.misc.CachePeriodProvider;
import org.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * A basic context for license consumer applications.
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
final class BasicLicenseConsumerContext
extends BasicLicenseApplicationContext
implements CachePeriodProvider,
        LicenseConsumerContext<ObfuscatedString>,
        LicenseProvider {

    BasicLicenseConsumerContext(BasicLicenseManagementContext context) {
        super(context);
    }

    @Override public long cachePeriodMillis() {
        return context().cachePeriodMillis();
    }

    @Override public License license() { return context().license(); }

    LicenseConsumerManager manager(
            Authentication authentication,
            Encryption encryption,
            Store store) {
        return manager(parameters(authentication, encryption), store);
    }

    private LicenseConsumerManager manager(
            final LicenseParameters lp,
            final Store store) {
        assert null != lp;
        requireNonNull(store);
        return new CachingManager() {
            @Override public LicenseParameters parameters() { return lp; }
            @Override public Store store() { return store; }
        };
    }

    private abstract class CachingManager extends CachingLicenseConsumerManager
    implements LicenseConsumerManager {

        final BasicLicenseConsumerContext cc = BasicLicenseConsumerContext.this;
        final String subject = cc.subject();
        final long cachePeriodMillis = cc.cachePeriodMillis();

        { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

        @Override public String subject() { return subject; }
        @Override public LicenseConsumerContext context() { return cc; }
        @Override public long cachePeriodMillis() { return cachePeriodMillis; }
    }

    LicenseConsumerManager ftpManager(
            LicenseConsumerManager parent,
            Authentication authentication,
            @CheckForNull Encryption encryption,
            Store secret,
            int days) {
        return chainedManager(
                parent,
                ftpParameters(parent, authentication, encryption, days),
                secret);
    }

    LicenseConsumerManager chainedManager(
            LicenseConsumerManager parent,
            Authentication authentication,
            @CheckForNull Encryption encryption,
            Store store) {
        return chainedManager(
                parent,
                chainedParameters(parent, authentication, encryption),
                store);
    }

    private LicenseConsumerManager chainedManager(
            final LicenseConsumerManager parent,
            final LicenseParameters lp,
            final Store store) {
        assert null != lp;
        requireNonNull(parent);
        requireNonNull(store);
        class Manager extends ChainedLicenseConsumerManager implements LicenseConsumerManager {

            final BasicLicenseConsumerContext cc = BasicLicenseConsumerContext.this;
            final String subject = cc.subject();
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override public String subject() { return subject; }
            @Override public LicenseConsumerContext context() { return cc; }
            @Override public long cachePeriodMillis() { return cachePeriodMillis; }
            @Override public License license() { return cc.license(); }
            @Override public LicenseParameters parameters() { return lp; }
            @Override public Store store() { return store; }
            @Override LicenseConsumerManager parent() { return parent; }
        }
        return new Manager();
    }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder<ObfuscatedString> manager() {
        @NotThreadSafe
        class MB implements ManagerBuilder<ObfuscatedString> {

            final BasicLicenseConsumerContext cc = BasicLicenseConsumerContext.this;

            @Nullable LicenseConsumerManager parent;
            int ftpDays;
            @Nullable Authentication authentication;
            @Nullable Encryption encryption;
            @Nullable Store store;

            @Override
            public LicenseConsumerManager build() {
                if (null == parent) return cc.manager(authentication, encryption, store);
                return 0 != ftpDays
                        ? cc.ftpManager(parent, authentication, encryption, store, ftpDays)
                        : cc.chainedManager(parent, authentication, encryption, store);
            }

            @Override
            public ManagerBuilder<ObfuscatedString> inject() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ManagerBuilder<ObfuscatedString> parent(final LicenseConsumerManager parent) {
                this.parent = parent;
                return this;
            }

            @Override
            public ManagerBuilder<ObfuscatedString> parent() {
                final MB target = this;
                return new MB() {
                    @Override public ManagerBuilder<ObfuscatedString> inject() {
                        return target.parent(build());
                    }
                };
            }

            @Override
            public ManagerBuilder<ObfuscatedString> ftpDays(final int ftpDays) {
                this.ftpDays = ftpDays;
                return this;
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
                    @Nullable ObfuscatedString storeProtection, keyProtection;

                    @Override
                    public ManagerBuilder<ObfuscatedString> inject() {
                        return authentication(
                                cc.keyStore(source, storeType, storeProtection, alias, keyProtection));
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
                        return loadFrom(cc.resource(name));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> storePassword(final ObfuscatedString storePassword) {
                        this.storeProtection = storePassword;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> alias(final String alias) {
                        this.alias = alias;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<ObfuscatedString>, ObfuscatedString> keyPassword(final ObfuscatedString keyPassword) {
                        this.keyProtection = keyPassword;
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
                        return encryption(cc.pbe(algorithm, password));
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

            @Override
            public ManagerBuilder<ObfuscatedString> storeIn(final Store store) {
                this.store = store;
                return this;
            }

            @Override
            public ManagerBuilder<ObfuscatedString> storeInSystemNode(final Class<?> classInPackage) {
                this.store = cc.systemNodeStore(classInPackage);
                return this;
            }

            @Override
            public ManagerBuilder<ObfuscatedString> storeInUserNode(final Class<?> classInPackage) {
                this.store = cc.userNodeStore(classInPackage);
                return this;
            }

            @Override
            public ManagerBuilder<ObfuscatedString> storeInPath(final Path path) {
                this.store = cc.pathStore(path);
                return this;
            }
        }
        return new MB();
    }
}

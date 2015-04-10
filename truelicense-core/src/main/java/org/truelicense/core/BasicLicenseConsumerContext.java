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
import org.truelicense.api.passwd.PasswordProtection;

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
final class BasicLicenseConsumerContext extends BasicLicenseApplicationContext
implements LicenseConsumerContext, CachePeriodProvider, LicenseProvider {

    BasicLicenseConsumerContext(BasicLicenseManagementContext context) {
        super(context);
    }

    @Override public long cachePeriodMillis() {
        return context().cachePeriodMillis();
    }

    @Override public License license() { return context().license(); }

    Authentication keyStore(
            @CheckForNull Source source,
            @CheckForNull String storeType,
            PasswordProtection storeProtection,
            String alias,
            @CheckForNull PasswordProtection keyProtection) {
        return context().authentication(keyStoreParameters(
                source, storeType, storeProtection, alias, keyProtection));
    }

    Encryption pbe(
            @CheckForNull String algorithm,
            PasswordProtection protection) {
        return context().encryption(pbeParameters(algorithm, protection));
    }

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
    @Override public ManagerBuilder manager() {
        @NotThreadSafe
        class MB implements ManagerBuilder {
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
            public ManagerBuilder inject() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ManagerBuilder parent(final LicenseConsumerManager parent) {
                this.parent = parent;
                return this;
            }

            @Override
            public ManagerBuilder parent() {
                final MB target = this;
                return new MB() {
                    @Override public ManagerBuilder inject() {
                        return target.parent(build());
                    }
                };
            }

            @Override
            public ManagerBuilder ftpDays(final int ftpDays) {
                this.ftpDays = ftpDays;
                return this;
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
                                cc.keyStore(source, storeType, storeProtection, alias, keyProtection));
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
                        return loadFrom(cc.resource(name));
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
                        return encryption(cc.pbe(algorithm, protection));
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

            @Override
            public ManagerBuilder storeIn(final Store store) {
                this.store = store;
                return this;
            }

            @Override
            public ManagerBuilder storeInSystemNode(final Class<?> classInPackage) {
                this.store = cc.systemNodeStore(classInPackage);
                return this;
            }

            @Override
            public ManagerBuilder storeInUserNode(final Class<?> classInPackage) {
                this.store = cc.userNodeStore(classInPackage);
                return this;
            }

            @Override
            public ManagerBuilder storeInPath(final Path path) {
                this.store = cc.pathStore(path);
                return this;
            }
        }
        return new MB();
    }
}

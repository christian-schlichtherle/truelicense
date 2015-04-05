/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.core.auth.Authentication;
import org.truelicense.core.crypto.Encryption;
import org.truelicense.core.io.Source;
import org.truelicense.core.io.Store;
import org.truelicense.core.util.CachePeriodProvider;
import org.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;

import static org.truelicense.core.util.Objects.requireNonNull;

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

    @Override public Authentication keyStore(
            @CheckForNull Source source,
            @CheckForNull String storeType,
            ObfuscatedString storePassword,
            String alias) {
        return context().authentication(kspUnchecked(
                source, storeType, storePassword, alias, null));
    }

    @Override public Authentication ftpKeyStore(
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
        return context().encryption(pbeUnchecked(algorithm, password));
    }

    @Override public LicenseConsumerManager manager(
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

    @Override public LicenseConsumerManager ftpManager(
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

    @Override public LicenseConsumerManager chainedManager(
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
        return new ChainedManager() {
            @Override public LicenseParameters parameters() { return lp; }
            @Override public Store store() { return store; }
            @Override LicenseConsumerManager parent() { return parent; }
        };
    }

    private abstract class ChainedManager extends ChainedLicenseConsumerManager
    implements LicenseConsumerManager {

        final BasicLicenseConsumerContext cc = BasicLicenseConsumerContext.this;
        final String subject = cc.subject();
        final long cachePeriodMillis = cc.cachePeriodMillis();

        { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

        @Override public String subject() { return subject; }
        @Override public LicenseConsumerContext context() { return cc; }
        @Override public long cachePeriodMillis() { return cachePeriodMillis; }
        @Override public License license() { return cc.license(); }
    }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder manager() {
        @NotThreadSafe
        class MB implements ManagerBuilder {
            final LicenseConsumerContext cc = BasicLicenseConsumerContext.this;

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
                    @Nullable ObfuscatedString storePassword, keyPassword;

                    @Override
                    public ManagerBuilder inject() {
                        return authentication(null != keyPassword
                                ? cc.ftpKeyStore(source, storeType, storePassword, alias, keyPassword)
                                : cc.keyStore(source, storeType, storePassword, alias));
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
                        return encryption(cc.pbe(algorithm, password));
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
            public ManagerBuilder storeInFile(final File file) {
                this.store = cc.fileStore(file);
                return this;
            }
        }
        return new MB();
    }
}

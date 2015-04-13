/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.BIOS;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.misc.CachePeriodProvider;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
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
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
@Immutable
final class BasicLicenseConsumerContext<PasswordSpecification>
extends BasicLicenseApplicationContext<PasswordSpecification>
implements CachePeriodProvider,
        LicenseConsumerContext<PasswordSpecification>,
        LicenseProvider {

    BasicLicenseConsumerContext(BasicLicenseManagementContext<PasswordSpecification> context) {
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

        class Manager extends CachingLicenseConsumerManager implements LicenseConsumerManager {

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override public BIOS bios() { return cc.bios(); }
            @Override public long cachePeriodMillis() { return cachePeriodMillis; }
            @Override public LicenseConsumerContext<PasswordSpecification> context() { return cc; }
            @Override public LicenseParameters parameters() { return lp; }
            @Override public Store store() { return store; }
            @Override public String subject() { return cc.subject(); }
        }
        return new Manager();
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

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override public BIOS bios() { return cc.bios(); }
            @Override public long cachePeriodMillis() { return cachePeriodMillis; }
            @Override public LicenseConsumerContext<PasswordSpecification> context() { return cc; }
            @Override public License license() { return cc.license(); }
            @Override public LicenseParameters parameters() { return lp; }
            @Override LicenseConsumerManager parent() { return parent; }
            @Override public Store store() { return store; }
            @Override public String subject() { return cc.subject(); }
        }
        return new Manager();
    }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder<PasswordSpecification> manager() {

        @NotThreadSafe
        @ParametersAreNullableByDefault
        class ChildManagerConfiguration implements ManagerBuilder<PasswordSpecification> {

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;

            @Nullable LicenseConsumerManager parent;
            int ftpDays;
            @Nullable Authentication authentication;
            @Nullable Encryption encryption;
            @Nullable Store store;

            @Override
            public LicenseConsumerManager build() {
                if (null == parent)
                    return cc.manager(authentication, encryption, store);
                return 0 != ftpDays
                        ? cc.ftpManager(parent, authentication, encryption, store, ftpDays)
                        : cc.chainedManager(parent, authentication, encryption, store);
            }

            @Override
            public ManagerBuilder<PasswordSpecification> inject() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> parent(final LicenseConsumerManager parent) {
                this.parent = parent;
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> parent() {

                class ParentManagerConfiguration extends ChildManagerConfiguration {
                    @Override public ManagerBuilder<PasswordSpecification> inject() {
                        return ChildManagerConfiguration.this.parent(build());
                    }
                }
                return new ParentManagerConfiguration();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> ftpDays(final int ftpDays) {
                this.ftpDays = ftpDays;
                return this;
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
                    @Nullable PasswordSpecification storeProtection, keyProtection;

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return authentication(
                                cc.keyStore(source, storeType, storeProtection, alias, keyProtection));
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
                        return loadFrom(cc.bios().resource(name, classLoader()));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storePassword(final PasswordSpecification storePassword) {
                        this.storeProtection = storePassword;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> alias(final String alias) {
                        this.alias = alias;
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyPassword(final PasswordSpecification keyPassword) {
                        this.keyProtection = keyPassword;
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
                        return encryption(cc.pbe(algorithm, password));
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

            @Override
            public ManagerBuilder<PasswordSpecification> storeIn(final Store store) {
                this.store = store;
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeInSystemNode(final Class<?> classInPackage) {
                this.store = cc.systemNodeStore(classInPackage);
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeInUserNode(final Class<?> classInPackage) {
                this.store = cc.userNodeStore(classInPackage);
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeInPath(final Path path) {
                this.store = cc.pathStore(path);
                return this;
            }
        }
        return new ChildManagerConfiguration();
    }
}

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
import org.truelicense.spi.misc.Option;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.nio.file.Path;
import java.util.List;

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
            List<Encryption> optionalEncryption,
            Store secret,
            int days) {
        return chainedManager(
                parent,
                ftpParameters(parent, authentication, optionalEncryption, days),
                secret);
    }

    LicenseConsumerManager chainedManager(
            LicenseConsumerManager parent,
            Authentication authentication,
            List<Encryption> optionalEncryption,
            Store store) {
        return chainedManager(
                parent,
                chainedParameters(parent, authentication, optionalEncryption),
                store);
    }

    private LicenseConsumerManager chainedManager(
            final LicenseConsumerManager parent,
            final LicenseParameters lp,
            final Store store) {

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
        class ChildManagerConfiguration implements ManagerBuilder<PasswordSpecification> {

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;

            List<LicenseConsumerManager> optionalParent = Option.none();
            int ftpDays;
            List<Authentication> optionalAuthentication = Option.none();
            List<Encryption> optionalEncryption = Option.none();
            List<Store> optionalStore = Option.none();

            @Override
            @SuppressWarnings("LoopStatementThatDoesntLoop")
            public LicenseConsumerManager build() {
                for (LicenseConsumerManager parent : optionalParent)
                    return 0 != ftpDays
                            ? cc.ftpManager(parent, optionalAuthentication.get(0), optionalEncryption, optionalStore.get(0), ftpDays)
                            : cc.chainedManager(parent, optionalAuthentication.get(0), optionalEncryption, optionalStore.get(0));
                return cc.manager(optionalAuthentication.get(0), optionalEncryption.get(0), optionalStore.get(0));
            }

            @Override
            public ManagerBuilder<PasswordSpecification> inject() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> parent(final LicenseConsumerManager parent) {
                this.optionalParent = Option.of(parent);
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
                this.optionalAuthentication = Option.of(authentication);
                return this;
            }

            @Override
            public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyStore() {

                class KeyStoreConfiguration implements KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {
                    List<String> optionalStoreType = Option.none(), optionalAlias = Option.none();
                    List<Source> optionalSource = Option.none();
                    List<PasswordSpecification> optionalStorePassword = Option.none(), optionalKeyPassword = Option.none();

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return authentication(
                                cc.keyStore(optionalSource, optionalStoreType, optionalStorePassword.get(0), optionalAlias.get(0), optionalKeyPassword));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storeType(final String storeType) {
                        this.optionalStoreType = Option.of(storeType);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFrom(final Source source) {
                        this.optionalSource = Option.of(source);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFromResource(String name) {
                        return loadFrom(cc.bios().resource(name, optionalClassLoader()));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storePassword(final PasswordSpecification storePassword) {
                        this.optionalStorePassword = Option.of(storePassword);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> alias(final String alias) {
                        this.optionalAlias = Option.of(alias);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyPassword(final PasswordSpecification keyPassword) {
                        this.optionalKeyPassword = Option.of(keyPassword);
                        return this;
                    }
                }
                return new KeyStoreConfiguration();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> encryption(final Encryption encryption) {
                this.optionalEncryption = Option.of(encryption);
                return this;
            }

            @Override
            public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> encryption() {

                class EncryptionConfiguration implements PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {
                    List<String> optionalAlgorithm = Option.none();
                    List<PasswordSpecification> optionalPassword = Option.none();

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return encryption(cc.pbe(optionalAlgorithm, optionalPassword.get(0)));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> algorithm(final String algorithm) {
                        this.optionalAlgorithm = Option.of(algorithm);
                        return this;
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> password(final PasswordSpecification password) {
                        this.optionalPassword = Option.of(password);
                        return this;
                    }
                }
                return new EncryptionConfiguration();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeIn(final Store store) {
                this.optionalStore = Option.of(store);
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeInPath(Path path) {
                return storeIn(cc.pathStore(path));
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeInSystemPreferences(Class<?> classInPackage) {
                return storeIn(cc.systemPreferencesStore(classInPackage));
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeInUserPreferences(Class<?> classInPackage) {
                return storeIn(cc.userPreferencesStore(classInPackage));
            }
        }
        return new ChildManagerConfiguration();
    }
}

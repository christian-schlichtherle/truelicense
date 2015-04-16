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

import java.nio.file.Path;
import java.util.List;

/**
 * A basic context for license consumer applications.
 * This class is immutable.
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
@SuppressWarnings("LoopStatementThatDoesntLoop")
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

    private LicenseConsumerManager chainedManager(
            final LicenseParameters parameters,
            final LicenseConsumerManager parent,
            final Store store) {

        class Manager extends ChainedLicenseConsumerManager implements LicenseConsumerManager {

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override public BIOS bios() { return cc.bios(); }
            @Override public long cachePeriodMillis() { return cachePeriodMillis; }
            @Override public LicenseConsumerContext<PasswordSpecification> context() { return cc; }
            @Override public License license() { return cc.license(); }
            @Override public LicenseParameters parameters() { return parameters; }
            @Override LicenseConsumerManager parent() { return parent; }
            @Override public Store store() { return store; }
            @Override public String subject() { return cc.subject(); }
        }
        return new Manager();
    }

    LicenseConsumerManager chainedManager(
            Authentication authentication,
            List<Encryption> optionalEncryption,
            LicenseConsumerManager parent,
            Store store) {
        return chainedManager(
                chainedParameters(authentication, optionalEncryption, parent),
                parent, store);
    }

    LicenseConsumerManager ftpManager(
            Authentication authentication,
            int days,
            List<Encryption> optionalEncryption,
            LicenseConsumerManager parent,
            Store secret) {
        return chainedManager(
                ftpParameters(authentication, days, optionalEncryption, parent),
                parent, secret);
    }

    @Override public License license() { return context().license(); }

    @SuppressWarnings("PackageVisibleField")
    @Override public ManagerBuilder<PasswordSpecification> manager() {

        class ChildManagerConfiguration implements ManagerBuilder<PasswordSpecification> {

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;

            int ftpDays;
            List<Authentication> optionalAuthentication = Option.none();
            List<Encryption> optionalEncryption = Option.none();
            List<LicenseConsumerManager> optionalParent = Option.none();
            List<Store> optionalStore = Option.none();

            @Override
            public ManagerBuilder<PasswordSpecification> authentication(final Authentication authentication) {
                this.optionalAuthentication = Option.wrap(authentication);
                return this;
            }

            @Override
            public LicenseConsumerManager build() {
                for (LicenseConsumerManager parent : optionalParent)
                    return 0 != ftpDays
                            ? cc.ftpManager(optionalAuthentication.get(0), ftpDays, optionalEncryption, parent, optionalStore.get(0))
                            : cc.chainedManager(optionalAuthentication.get(0), optionalEncryption, parent, optionalStore.get(0));
                return cc.manager(optionalAuthentication.get(0), optionalEncryption.get(0), optionalStore.get(0));
            }

            @Override
            public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> encryption() {

                class EncryptionConfiguration implements PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {

                    List<String> optionalAlgorithm = Option.none();
                    List<PasswordSpecification> password = Option.none();

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> algorithm(final String algorithm) {
                        this.optionalAlgorithm = Option.wrap(algorithm);
                        return this;
                    }

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return encryption(cc.passwordBasedEncryption(optionalAlgorithm, password.get(0)));
                    }

                    @Override
                    public PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> password(final PasswordSpecification password) {
                        this.password = Option.wrap(password);
                        return this;
                    }
                }
                return new EncryptionConfiguration();
            }

            @Override
            public ManagerBuilder<PasswordSpecification> encryption(final Encryption encryption) {
                this.optionalEncryption = Option.wrap(encryption);
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> ftpDays(final int ftpDays) {
                this.ftpDays = ftpDays;
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> inject() {
                throw new UnsupportedOperationException();
            }

            @Override
            public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyStore() {

                class KeyStoreConfiguration implements KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> {

                    List<String> alias = Option.none();
                    List<String> optionalAlgorithm = Option.none();
                    List<PasswordSpecification> optionalKeyPassword = Option.none();
                    List<Source> optionalSource = Option.none();
                    List<String> optionalStoreType = Option.none();
                    List<PasswordSpecification> storePassword = Option.none();

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> algorithm(final String algorithm) {
                        this.optionalAlgorithm = Option.wrap(algorithm);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> alias(final String alias) {
                        this.alias = Option.wrap(alias);
                        return this;
                    }

                    @Override
                    public ManagerBuilder<PasswordSpecification> inject() {
                        return authentication(
                                cc.keyStoreAuthentication(alias.get(0), optionalAlgorithm, optionalKeyPassword, optionalSource, optionalStoreType, storePassword.get(0)));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyPassword(final PasswordSpecification keyPassword) {
                        this.optionalKeyPassword = Option.wrap(keyPassword);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFrom(final Source source) {
                        this.optionalSource = Option.wrap(source);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> loadFromResource(String name) {
                        return loadFrom(cc.resource(name));
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storePassword(final PasswordSpecification storePassword) {
                        this.storePassword = Option.wrap(storePassword);
                        return this;
                    }

                    @Override
                    public KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> storeType(final String storeType) {
                        this.optionalStoreType = Option.wrap(storeType);
                        return this;
                    }
                }
                return new KeyStoreConfiguration();
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
            public ManagerBuilder<PasswordSpecification> parent(final LicenseConsumerManager parent) {
                this.optionalParent = Option.wrap(parent);
                return this;
            }

            @Override
            public ManagerBuilder<PasswordSpecification> storeIn(final Store store) {
                this.optionalStore = Option.wrap(store);
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

    private LicenseConsumerManager manager(
            final LicenseParameters parameters,
            final Store store) {

        class Manager extends CachingLicenseConsumerManager implements LicenseConsumerManager {

            final BasicLicenseConsumerContext<PasswordSpecification> cc = BasicLicenseConsumerContext.this;
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override public BIOS bios() { return cc.bios(); }
            @Override public long cachePeriodMillis() { return cachePeriodMillis; }
            @Override public LicenseConsumerContext<PasswordSpecification> context() { return cc; }
            @Override public LicenseParameters parameters() { return parameters; }
            @Override public Store store() { return store; }
            @Override public String subject() { return cc.subject(); }
        }
        return new Manager();
    }

    LicenseConsumerManager manager(
            Authentication authentication,
            Encryption encryption,
            Store store) {
        return manager(parameters(authentication, encryption), store);
    }
}

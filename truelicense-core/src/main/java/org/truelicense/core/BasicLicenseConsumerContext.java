/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.BIOS;
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
            List<Encryption> encryption,
            LicenseConsumerManager parent,
            Store store) {
        return chainedManager(
                chainedParameters(authentication, encryption, parent),
                parent, store);
    }

    LicenseConsumerManager ftpManager(
            Authentication authentication,
            int days,
            List<Encryption> encryption,
            LicenseConsumerManager parent,
            Store secret) {
        return chainedManager(
                ftpParameters(authentication, days, encryption, parent),
                parent, secret);
    }

    @Override public License license() { return context().license(); }

    @Override public ChildManagerConfiguration manager() {
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

    class ChildManagerConfiguration implements ManagerBuilder<PasswordSpecification> {

        int ftpDays;
        List<Authentication> authentication = Option.none();
        List<Encryption> encryption = Option.none();
        List<LicenseConsumerManager> parent = Option.none();
        List<Store> store = Option.none();

        @Override
        public ChildManagerConfiguration authentication(final Authentication authentication) {
            this.authentication = Option.wrap(authentication);
            return this;
        }

        @Override
        public LicenseConsumerManager build() {
            for (LicenseConsumerManager parent : this.parent)
                return 0 != ftpDays
                        ? ftpManager(authentication.get(0), ftpDays, encryption, parent, store.get(0))
                        : chainedManager(authentication.get(0), encryption, parent, store.get(0));
            return manager(authentication.get(0), encryption.get(0), store.get(0));
        }

        @Override
        public PbeConsumerConfiguration encryption() {
            return new PbeConsumerConfiguration();
        }

        @Override
        public ChildManagerConfiguration encryption(final Encryption encryption) {
            this.encryption = Option.wrap(encryption);
            return this;
        }

        @Override
        public ChildManagerConfiguration ftpDays(final int ftpDays) {
            this.ftpDays = ftpDays;
            return this;
        }

        @Override
        public ChildManagerConfiguration inject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public KsbaConsumerConfiguration keyStore() {
            return new KsbaConsumerConfiguration();
        }

        @Override
        public ParentManagerConfiguration parent() {
            return new ParentManagerConfiguration();
        }

        @Override
        public ChildManagerConfiguration parent(final LicenseConsumerManager parent) {
            this.parent = Option.wrap(parent);
            return this;
        }

        @Override
        public ChildManagerConfiguration storeIn(final Store store) {
            this.store = Option.wrap(store);
            return this;
        }

        @Override
        public ChildManagerConfiguration storeInPath(Path path) {
            return storeIn(pathStore(path));
        }

        @Override
        public ChildManagerConfiguration storeInSystemPreferences(Class<?> classInPackage) {
            return storeIn(systemPreferencesStore(classInPackage));
        }

        @Override
        public ChildManagerConfiguration storeInUserPreferences(Class<?> classInPackage) {
            return storeIn(userPreferencesStore(classInPackage));
        }

        final class KsbaConsumerConfiguration
                extends KsbaConfiguration<ChildManagerConfiguration>{

            @Override
            public ChildManagerConfiguration inject() {
                return authentication(build());
            }
        }

        final class ParentManagerConfiguration extends ChildManagerConfiguration {

            @Override public ChildManagerConfiguration inject() {
                return ChildManagerConfiguration.this.parent(build());
            }
        }

        final class PbeConsumerConfiguration
                extends PbeConfiguration<ChildManagerConfiguration> {

            @Override
            public ChildManagerConfiguration inject() {
                return encryption(build());
            }
        }
    }
}

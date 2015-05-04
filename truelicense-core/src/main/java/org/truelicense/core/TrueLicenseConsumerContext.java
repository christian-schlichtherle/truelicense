/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.io.Store;
import org.truelicense.api.io.Transformation;
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
final class TrueLicenseConsumerContext<Model, PasswordSpecification>
extends TrueLicenseApplicationContext<Model, PasswordSpecification>
implements LicenseConsumerContext<PasswordSpecification> {

    TrueLicenseConsumerContext(TrueLicenseManagementContext<Model, PasswordSpecification> context) {
        super(context);
    }

    private LicenseConsumerManager chainedManager(
            final TrueLicenseParameters parameters,
            final LicenseConsumerManager parent,
            final Store store) {

        class Manager extends ChainedLicenseConsumerManager
        implements LicenseConsumerManager {

            final TrueLicenseConsumerContext<Model, PasswordSpecification> cc = TrueLicenseConsumerContext.this;
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override
            public long cachePeriodMillis() { return cachePeriodMillis; }

            @Override
            public LicenseConsumerContext<PasswordSpecification> context() { return cc; }

            @Override
            public License license() { return cc.license(); }

            @Override
            public TrueLicenseParameters parameters() { return parameters; }

            @Override
            LicenseConsumerManager parent() { return parent; }

            @Override
            public Store store() { return store; }
        }
        return new Manager();
    }

    LicenseConsumerManager chainedManager(
            Authentication authentication,
            List<Transformation> encryption,
            LicenseConsumerManager parent,
            Store store) {
        return chainedManager(
                chainedParameters(authentication, encryption, parent),
                parent, store);
    }

    LicenseConsumerManager ftpManager(
            Authentication authentication,
            int days,
            List<Transformation> encryption,
            LicenseConsumerManager parent,
            Store secret) {
        return chainedManager(
                ftpParameters(authentication, days, encryption, parent),
                parent, secret);
    }

    @Override
    public ChildLicenseConsumerManagerBuilder manager() {
        return new ChildLicenseConsumerManagerBuilder();
    }

    private LicenseConsumerManager manager(
            final TrueLicenseParameters parameters,
            final Store store) {

        class Manager extends CachingLicenseConsumerManager
        implements LicenseConsumerManager {

            final TrueLicenseConsumerContext<Model, PasswordSpecification> cc = TrueLicenseConsumerContext.this;
            final long cachePeriodMillis = cc.cachePeriodMillis();

            { if (0 > cachePeriodMillis) throw new IllegalArgumentException(); }

            @Override
            public long cachePeriodMillis() { return cachePeriodMillis; }

            @Override
            public LicenseConsumerContext<PasswordSpecification> context() { return cc; }

            @Override
            public TrueLicenseParameters parameters() { return parameters; }

            @Override
            public Store store() { return store; }
        }
        return new Manager();
    }

    LicenseConsumerManager manager(
            Authentication authentication,
            Transformation encryption,
            Store store) {
        return manager(parameters(authentication, encryption), store);
    }

    class ChildLicenseConsumerManagerBuilder
    extends TrueLicenseManagerBuilder<ChildLicenseConsumerManagerBuilder>
    implements LicenseConsumerManagerBuilder<PasswordSpecification> {

        int ftpDays;
        List<LicenseConsumerManager> parent = Option.none();
        List<Store> store = Option.none();

        @Override
        public LicenseConsumerManager build() {
            for (LicenseConsumerManager parent : this.parent)
                return 0 != ftpDays
                        ? ftpManager(authentication.get(0), ftpDays, encryption, parent, store.get(0))
                        : chainedManager(authentication.get(0), encryption, parent, store.get(0));
            return manager(authentication.get(0), encryption.get(0), store.get(0));
        }

        public ChildLicenseConsumerManagerBuilder ftpDays(final int ftpDays) {
            this.ftpDays = ftpDays;
            return this;
        }

        @Override
        public ChildLicenseConsumerManagerBuilder inject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ParentLicenseConsumerManagerBuilder parent() {
            return new ParentLicenseConsumerManagerBuilder();
        }

        @Override
        public ChildLicenseConsumerManagerBuilder parent(final LicenseConsumerManager parent) {
            this.parent = Option.wrap(parent);
            return this;
        }

        public ChildLicenseConsumerManagerBuilder storeIn(final Store store) {
            this.store = Option.wrap(store);
            return this;
        }

        public ChildLicenseConsumerManagerBuilder storeInPath(Path path) {
            return storeIn(pathStore(path));
        }

        public ChildLicenseConsumerManagerBuilder storeInSystemPreferences(Class<?> classInPackage) {
            return storeIn(systemPreferencesStore(classInPackage));
        }

        public ChildLicenseConsumerManagerBuilder storeInUserPreferences(Class<?> classInPackage) {
            return storeIn(userPreferencesStore(classInPackage));
        }

        final class ParentLicenseConsumerManagerBuilder
        extends ChildLicenseConsumerManagerBuilder {

            @Override
            public ChildLicenseConsumerManagerBuilder inject() {
                return ChildLicenseConsumerManagerBuilder.this.parent(build());
            }
        }
    }
}

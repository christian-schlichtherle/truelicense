/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.api.misc.ContextProvider;

import java.util.Objects;

/**
 * Adapts vendor and consumer license managers so that they may generally throw
 * an {@link UncheckedLicenseManagementException} rather than a (checked)
 * {@link LicenseManagementException}.
 *
 * @author Christian Schlichtherle
 */
public final class UncheckedManager {

    private UncheckedManager() { }

    /**
     * Returns a vendor license manager which may generally throw an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the vendor license manager to adapt.
     */
    public static UncheckedVendorLicenseManager from(VendorLicenseManager manager) {
        return new UncheckedVendorTrueLicenseManager(manager);
    }

    /**
     * Returns a consumer license manager which may generally throw an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the consumer license manager to adapt.
     */
    public static UncheckedConsumerLicenseManager from(ConsumerLicenseManager manager) {
        return new UncheckedConsumerTrueLicenseManager(manager);
    }

    static <V> V runUnchecked(final CheckedTask<V> task) {
        try {
            return task.run();
        } catch (LicenseManagementException e) {
            throw new UncheckedLicenseManagementException(e);
        }
    }

    private static final class UncheckedVendorTrueLicenseManager
    extends UncheckedTrueLicenseManager<VendorLicenseManager>
    implements UncheckedVendorLicenseManager {

        UncheckedVendorTrueLicenseManager(VendorLicenseManager manager) {
            super(manager);
        }

        @Override
        public UncheckedLicenseKeyGenerator generateKeyFrom(final License bean) {
            return new UncheckedLicenseKeyGenerator() {

                final LicenseKeyGenerator generator = manager.generateKeyFrom(bean);

                @Override
                public License license() throws UncheckedLicenseManagementException {
                    return runUnchecked(generator::license);
                }

                @Override
                public LicenseKeyGenerator saveTo(Sink sink) throws UncheckedLicenseManagementException {
                    return runUnchecked(() -> generator.saveTo(sink));
                }
            };
        }
    }

    private static final class UncheckedConsumerTrueLicenseManager
    extends UncheckedTrueLicenseManager<ConsumerLicenseManager>
    implements UncheckedConsumerLicenseManager {

        UncheckedConsumerTrueLicenseManager(ConsumerLicenseManager manager) {
            super(manager);
        }

        @Override
        public void install(final Source source) throws UncheckedLicenseManagementException {
            runUnchecked(() -> {
                manager.install(source);
                return null;
            });
        }

        @Override
        public License load() throws UncheckedLicenseManagementException {
            return runUnchecked(manager::load);
        }

        @Override
        public void verify() throws UncheckedLicenseManagementException {
            runUnchecked(() -> {
                manager.verify();
                return null;
            });
        }

        @Override
        public void uninstall() throws UncheckedLicenseManagementException {
            runUnchecked(() -> {
                manager.uninstall();
                return null;
            });
        }
    }

    private static abstract class UncheckedTrueLicenseManager<
            M extends ContextProvider<LicenseManagementContext> & LicenseManagementParametersProvider>
    implements
            ContextProvider<LicenseManagementContext>,
            LicenseManagementParametersProvider {

        final M manager;

        UncheckedTrueLicenseManager(final M manager) {
            this.manager = Objects.requireNonNull(manager);
        }

        public M checked() { return manager; }

        @Override
        public LicenseManagementContext context() { return manager.context(); }

        @Override
        public LicenseManagementParameters parameters() {
            return manager.parameters();
        }
    }

    private interface CheckedTask<V> {

        V run() throws LicenseManagementException;
    }
}

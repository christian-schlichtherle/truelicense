/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;
import net.truelicense.api.misc.ContextProvider;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Adapts vendor and consumer license managers so that they may generally throw an
 * {@link UncheckedLicenseManagementException} rather than a (checked) {@link LicenseManagementException}.
 *
 * @deprecated since TrueLicense 3.1.0. Use {@link VendorLicenseManager#unchecked()} or
 *             {@link ConsumerLicenseManager#unchecked()} instead.
 * @author Christian Schlichtherle
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class UncheckedLicenseManager {

    private UncheckedLicenseManager() { }

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

    private static <V> V uncheck(final Callable<V> task) {
        try { return task.call(); }
        catch (RuntimeException e) { throw e; }
        catch (Exception e) { throw new UncheckedLicenseManagementException(e); }
    }

    private static final class UncheckedVendorTrueLicenseManager
            extends UncheckedTrueLicenseManager<VendorLicenseManager>
            implements UncheckedVendorLicenseManager {

        UncheckedVendorTrueLicenseManager(VendorLicenseManager manager) {
            super(manager);
        }

        @Override
        public UncheckedLicenseKeyGenerator generateKeyFrom(final License bean)
                throws UncheckedLicenseManagementException {
            return uncheck(() -> new UncheckedLicenseKeyGenerator() {
                final LicenseKeyGenerator generator = checked().generateKeyFrom(bean);

                @Override
                public License license() throws UncheckedLicenseManagementException {
                    return uncheck(generator::license);
                }

                @Override
                public LicenseKeyGenerator saveTo(Sink sink) throws UncheckedLicenseManagementException {
                    return uncheck(() -> generator.saveTo(sink));
                }
            });
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
            uncheck(() -> {
                checked().install(source);
                return null;
            });
        }

        @Override
        public License load() throws UncheckedLicenseManagementException {
            return uncheck(checked()::load);
        }

        @Override
        public void verify() throws UncheckedLicenseManagementException {
            uncheck(() -> {
                checked().verify();
                return null;
            });
        }

        @Override
        public void uninstall() throws UncheckedLicenseManagementException {
            uncheck(() -> {
                checked().uninstall();
                return null;
            });
        }
    }

    private static abstract class UncheckedTrueLicenseManager<M extends ContextProvider<LicenseManagementContext> & LicenseManagementParametersProvider>
            implements ContextProvider<LicenseManagementContext>, LicenseManagementParametersProvider {

        private final M manager;

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
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import global.namespace.fun.io.api.Socket;
import net.truelicense.api.misc.ContextProvider;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * Adapts vendor and consumer license managers so that they generally throw an
 * {@link UncheckedLicenseManagementException} instead of a (checked) {@link LicenseManagementException} if an
 * operation fails.
 *
 * @see ConsumerLicenseManager#unchecked()
 * @see VendorLicenseManager#unchecked()
 * @author Christian Schlichtherle
 */
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
        return (UncheckedVendorTrueLicenseManager) () -> manager;
    }

    /**
     * Returns a consumer license manager which may generally throw an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the consumer license manager to adapt.
     */
    public static UncheckedConsumerLicenseManager from(ConsumerLicenseManager manager) {
        return (UncheckedConsumerTrueLicenseManager) () -> manager;
    }

    private static <V> V uncheck(Callable<V> task) {
        try { return task.call(); }
        catch (RuntimeException e) { throw e; }
        catch (Exception e) { throw new UncheckedLicenseManagementException(e); }
    }

    private interface UncheckedVendorTrueLicenseManager
            extends UncheckedTrueLicenseManager<VendorLicenseManager>, UncheckedVendorLicenseManager {

        @Override
        default UncheckedLicenseKeyGenerator generateKeyFrom(License bean)
                throws UncheckedLicenseManagementException {
            return uncheck(() -> new UncheckedLicenseKeyGenerator() {
                final LicenseKeyGenerator generator = checked().generateKeyFrom(bean);

                @Override
                public License license() throws UncheckedLicenseManagementException {
                    return uncheck(generator::license);
                }

                @Override
                public LicenseKeyGenerator saveTo(Socket<OutputStream> output) throws UncheckedLicenseManagementException {
                    return uncheck(() -> generator.saveTo(output));
                }
            });
        }
    }

    private interface UncheckedConsumerTrueLicenseManager
            extends UncheckedTrueLicenseManager<ConsumerLicenseManager>, UncheckedConsumerLicenseManager {

        @Override
        default void install(Socket<InputStream> input) throws UncheckedLicenseManagementException {
            uncheck(() -> {
                checked().install(input);
                return null;
            });
        }

        @Override
        default License load() throws UncheckedLicenseManagementException {
            return uncheck(checked()::load);
        }

        @Override
        default void verify() throws UncheckedLicenseManagementException {
            uncheck(() -> {
                checked().verify();
                return null;
            });
        }

        @Override
        default void uninstall() throws UncheckedLicenseManagementException {
            uncheck(() -> {
                checked().uninstall();
                return null;
            });
        }
    }

    private interface UncheckedTrueLicenseManager<M extends ContextProvider<LicenseManagementContext> & LicenseManagementParametersProvider>
            extends ContextProvider<LicenseManagementContext>, LicenseManagementParametersProvider {

        @Override
        default LicenseManagementContext context() { return checked().context(); }

        @Override
        default LicenseManagementParameters parameters() { return checked().parameters(); }

        M checked();
    }
}

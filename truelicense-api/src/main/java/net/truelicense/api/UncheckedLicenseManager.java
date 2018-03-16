/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.api.Transformation;
import net.truelicense.api.auth.Authentication;

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
        return (TrueUncheckedVendorLicenseManager) () -> manager;
    }

    /**
     * Returns a consumer license manager which may generally throw an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the consumer license manager to adapt.
     */
    public static UncheckedConsumerLicenseManager from(ConsumerLicenseManager manager) {
        return (TrueUncheckedConsumerLicenseManager) () -> manager;
    }

    private static <V> V callUnchecked(Callable<V> task) {
        try {
            return task.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UncheckedLicenseManagementException(e);
        }
    }

    private interface TrueUncheckedVendorLicenseManager
            extends UncheckedLicenseManagementSchema<VendorLicenseManager>, UncheckedVendorLicenseManager {

        @Override
        default UncheckedLicenseKeyGenerator generateKeyFrom(License bean)
                throws UncheckedLicenseManagementException {
            return callUnchecked(() -> new UncheckedLicenseKeyGenerator() {
                final LicenseKeyGenerator generator = checked().generateKeyFrom(bean);

                @Override
                public License license() throws UncheckedLicenseManagementException {
                    return callUnchecked(generator::license);
                }

                @Override
                public UncheckedLicenseKeyGenerator saveTo(Sink sink) throws UncheckedLicenseManagementException {
                    callUnchecked(() -> generator.saveTo(sink));
                    return this;
                }
            });
        }
    }

    private interface TrueUncheckedConsumerLicenseManager
            extends UncheckedLicenseManagementSchema<ConsumerLicenseManager>, UncheckedConsumerLicenseManager {

        @Override
        default void install(Source source) throws UncheckedLicenseManagementException {
            callUnchecked(() -> {
                checked().install(source);
                return null;
            });
        }

        @Override
        default License load() throws UncheckedLicenseManagementException {
            return callUnchecked(checked()::load);
        }

        @Override
        default void verify() throws UncheckedLicenseManagementException {
            callUnchecked(() -> {
                checked().verify();
                return null;
            });
        }

        @Override
        default void uninstall() throws UncheckedLicenseManagementException {
            callUnchecked(() -> {
                checked().uninstall();
                return null;
            });
        }
    }

    private interface UncheckedLicenseManagementSchema<M extends LicenseManagementSchema>
            extends LicenseManagementSchema {

        @Override
        default Authentication authentication() { return checked().authentication(); }

        @Override
        default LicenseManagementContext context() { return checked().context(); }

        @Override
        default Transformation encryption() { return checked().encryption(); }

        M checked();
    }
}

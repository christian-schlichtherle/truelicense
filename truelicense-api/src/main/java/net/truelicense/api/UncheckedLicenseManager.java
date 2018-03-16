/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Source;

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
        return (Vendor<VendorLicenseManager>) () -> manager;
    }

    /**
     * Returns a consumer license manager which may generally throw an
     * {@link UncheckedLicenseManagementException} rather than a (checked)
     * {@link LicenseManagementException}.
     *
     * @param manager the consumer license manager to adapt.
     */
    public static UncheckedConsumerLicenseManager from(ConsumerLicenseManager manager) {
        return (Consumer<ConsumerLicenseManager>) () -> manager;
    }

    static <V> V callUnchecked(Callable<V> task) {
        try {
            return task.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UncheckedLicenseManagementException(e);
        }
    }

    public interface Vendor<M extends VendorLicenseManager> extends HasSchema<M>, UncheckedVendorLicenseManager {

        @Override
        default UncheckedLicenseKeyGenerator generateKeyFrom(License bean) throws UncheckedLicenseManagementException {
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

    public interface Consumer<M extends ConsumerLicenseManager> extends HasSchema<M>, UncheckedConsumerLicenseManager {

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

    private interface HasSchema<M extends HasLicenseManagementSchema> extends HasLicenseManagementSchema {

        @Override
        default LicenseManagementSchema schema() { return checked().schema(); }

        M checked();
    }
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.fun.io.api.Source;

/**
 * Defines the life cycle management operations for license keys in consumer applications.
 * <p>
 * An unchecked consumer license manager generally throws an {@link UncheckedLicenseManagementException} with a
 * (checked) {@link LicenseManagementException} as its cause if an operation fails.
 *
 * <h2>How to Preview License Keys</h2>
 * <p>
 * This interface intentionally lacks a method to preview the license bean which is encoded in a license key.
 * You can work around this constraint by using a {@linkplain LicenseManagementContext license management context} to
 * configure a consumer license manager which uses a transient memory store instead of a persistent store - see
 * {@link ConsumerLicenseManagerBuilder#storeIn}.
 * Once configured, you can {@linkplain #install install} the license key to the transient memory store and
 * {@linkplain #load load} its encoded license bean.
 *
 * @see ConsumerLicenseManager#unchecked()
 */
public interface UncheckedConsumerLicenseManager extends ConsumerLicenseManager {

    @Override
    default LicenseManagerParameters parameters() {
        return checked().parameters();
    }

    /**
     * Verifies the digital signature of the license key in the given source and copies it to the configured store.
     * Unlike {@link #verify}, this operation does <em>not</em> validate the encoded license bean.
     * This enables the caller to obtain a duplicate of the license bean even if its validation would fail, e.g. if the
     * license has expired.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseManagementAuthorization#clearInstall authorization check}.
     *
     * @param source the source to read the license key from.
     */
    default void install(Source source) throws UncheckedLicenseManagementException {
        UncheckedLicenseManager.callUnchecked(() -> {
            checked().install(source);
            return null;
        });
    }

    /**
     * Loads the installed license key and returns an unvalidated duplicate of its encoded license bean.
     * Unlike {@link #verify}, this operation does <em>not</em> validate the encoded license bean.
     * This enables the caller to obtain a duplicate of the license bean even if its validation would fail, e.g. if the
     * license has expired.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseManagementAuthorization#clearLoad authorization check}.
     *
     * @return An unvalidated duplicate of the license bean which is encoded in the installed license key.
     */
    default License load() throws UncheckedLicenseManagementException {
        return UncheckedLicenseManager.callUnchecked(checked()::load);
    }

    /**
     * Loads the installed license key and verifies its encoded license bean.
     * You should call this method whenever you want to verify access to a feature of your software product.
     * Execution needs to be fast in order to support frequent calling.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseManagementAuthorization#clearVerify authorization check}.
     *
     * @throws UncheckedLicenseManagementException with a {@link LicenseValidationException} as its cause if validating
     *                                             the license bean fails, e.g. if the license has expired.
     */
    default void verify() throws UncheckedLicenseManagementException {
        UncheckedLicenseManager.callUnchecked(() -> {
            checked().verify();
            return null;
        });
    }

    /**
     * Uninstalls the installed license key.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseManagementAuthorization#clearUninstall authorization check}.
     */
    default void uninstall() throws UncheckedLicenseManagementException {
        UncheckedLicenseManager.callUnchecked(() -> {
            checked().uninstall();
            return null;
        });
    }

    @Override
    default UncheckedConsumerLicenseManager unchecked() {
        return this;
    }

    /**
     * Adapts this consumer license manager so that it generally throws a (checked) {@link LicenseManagementException}
     * instead of an {@link UncheckedLicenseManagementException} if an operation fails.
     *
     * @return the adapted (checked) consumer license manager.
     */
    ConsumerLicenseManager checked();
}

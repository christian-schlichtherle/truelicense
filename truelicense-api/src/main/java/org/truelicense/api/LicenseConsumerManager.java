/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Source;
import org.truelicense.api.misc.ContextProvider;

/**
 * Defines the life cycle management operations for license keys in consumer
 * applications.
 *
 * <h3>How to Preview License Keys?</h3>
 * <p>
 * This interface lacks a method to preview the license bean which is encoded
 * in a license key.
 * However, you can work around this constraint by using a
 * {@linkplain LicenseConsumerContext license consumer context}
 * to configure a license consumer manager which uses a transient
 * {@link org.truelicense.api.io.BIOS#memoryStore()} instead of a persistent
 * store - see {@link LicenseConsumerManagerBuilder#storeIn}.
 * Once configured, you can {@linkplain #install install} the license key to
 * the transient memory store and {@linkplain #view view} its encoded license
 * bean.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseConsumerManager
extends ContextProvider<LicenseConsumerContext<?>>,
        LicenseParametersProvider,
        LicenseSubjectProvider {

    /**
     * Verifies the digital signature of the license key and copies it to the
     * configured store.
     * Unlike {@link #verify}, this operation does not validate the license
     * bean.
     * This enables the caller to obtain a duplicate of the license bean even
     * if its validation would fail, e.g. if the license has expired.
     * Note that this is a change of the behavior from versions prior to
     * TrueLicense 2.4 where license validation was mandatory.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseAuthorization#clearInstall authorization check}.
     *
     * @param  source the source for loading the license key.
     */
    void install(Source source) throws LicenseManagementException;

    /**
     * Returns an unvalidated duplicate of the license bean which is encoded in
     * the installed license key.
     * You should call this method whenever you want to display the properties
     * of the installed license.
     * Unlike {@link #verify}, this operation does <em>not</em> validate the
     * license bean.
     * This enables the caller to obtain a duplicate of the license bean even
     * if its validation would fail, e.g. if the license has expired.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseAuthorization#clearView authorization check}.
     *
     * @return An unvalidated duplicate of the license bean which is encoded in
     *         the installed license key.
     */
    License view() throws LicenseManagementException;

    /**
     * Verifies the license bean which is encoded in the installed license key.
     * You should call this method whenever you want to verify access to a
     * feature of your software product.
     * Execution needs to be fast in order to support frequent calling.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseAuthorization#clearVerify authorization check}.
     *
     * @throws LicenseValidationException if validating the license bean fails,
     *         e.g. if the license has expired.
     */
    void verify() throws LicenseManagementException;

    /**
     * Uninstalls the installed license key.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseAuthorization#clearUninstall authorization check}.
     */
    void uninstall() throws LicenseManagementException;
}

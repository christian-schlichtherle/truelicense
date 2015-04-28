/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.misc.ContextProvider;

/**
 * Defines the life cycle management operations for license keys in vendor
 * applications alias key generators.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseVendorManager
        extends ContextProvider<LicenseVendorContext<?>>,
        LicenseSubjectProvider,
        LicenseParametersProvider {

    /**
     * Returns a license key generator for the given license bean.
     * <p/>
     * Calling this operation performs an initial
     * {@linkplain LicenseAuthorization#clearCreate authorization check}.
     *
     * @param bean the license bean to process.
     *             The bean is not modified by this method.
     * @return A license key generator for the given license bean.
     * @throws LicenseValidationException if validating the license bean fails,
     *                                    e.g. if the license has expired.
     */
    LicenseKeyGenerator generator(License bean) throws LicenseManagementException;
}

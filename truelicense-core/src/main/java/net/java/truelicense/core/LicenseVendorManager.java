/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core;

import net.java.truelicense.core.io.Sink;
import net.java.truelicense.core.util.ContextProvider;

/**
 * Defines the life cycle management operations for license keys in vendor
 * applications alias key tools.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseVendorManager
extends ContextProvider<LicenseVendorContext>,
        LicenseSubjectProvider,
        LicenseParametersProvider {

    /**
     * Generates a license key from the given license bean and stores the
     * result to the given sink.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseAuthorization#clearCreate authorization check}.
     *
     * @param  bean the license bean to process.
     *         The bean is not modified by this method.
     * @param  sink the sink for storing the license key.
     * @return A duplicate of the
     *         {@linkplain LicenseInitialization#initialize initialized}
     *         and
     *         {@linkplain LicenseValidation#validate validated} license bean
     *         which has been stored in the license key.
     * @throws LicenseValidationException if validating the license bean fails,
     *         e.g. if the license has expired.
     */
    License create(License bean, Sink sink) throws LicenseManagementException;
}

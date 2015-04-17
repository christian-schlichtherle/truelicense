/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.codec.CodecProvider;

/**
 * A derived context for license vendor applications alias license key tools.
 * Use this context to configure a {@link LicenseVendorManager} with the
 * required parameters.
 * For a demonstration of this API, please use the TrueLicense Maven Archetype
 * to generate a sample project - even if you don't use Maven to build your
 * software product.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface LicenseVendorContext<PasswordSpecification>
extends CodecProvider,
        LicenseApplicationContext,
        LicenseProvider {

    /**
     * Returns a builder for a
     * {@linkplain LicenseVendorManager license vendor manager}.
     * Call its {@link LicenseVendorManagerBuilder#build} method to obtain
     * the configured license vendor manager.
     */
    LicenseVendorManagerBuilder<PasswordSpecification> manager();
}

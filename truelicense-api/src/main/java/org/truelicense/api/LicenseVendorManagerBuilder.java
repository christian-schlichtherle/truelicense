/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.misc.Builder;

/**
 * A builder for {@linkplain LicenseVendorManager license vendor managers}.
 * Call its {@link #build} method to obtain the configured license vendor
 * manager.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface LicenseVendorManagerBuilder<PasswordSpecification>
        extends Builder<LicenseVendorManager>,
                LicenseManagerBuilder<PasswordSpecification,
                                      LicenseVendorManagerBuilder<PasswordSpecification>> {
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.misc.Builder;

/**
 * A builder for {@linkplain VendorLicenseManager vendor license managers}.
 * Call its {@link #build} method to obtain the configured vendor license
 * manager.
 *
 * @author Christian Schlichtherle
 */
public interface VendorLicenseManagerBuilder
extends Builder<VendorLicenseManager>,
        LicenseManagerBuilder<VendorLicenseManagerBuilder> {
}

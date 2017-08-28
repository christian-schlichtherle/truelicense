/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.misc.Builder;

/**
 * A builder for {@linkplain VendorLicenseManager vendor license managers}.
 * Call its {@link #build} method to obtain the configured vendor license
 * manager.
 * <p>
 * Clients should not implement this interface because it's subject to expansion
 * in future minor version updates.
 *
 * @author Christian Schlichtherle
 */
public interface VendorLicenseManagerBuilder
extends Builder<VendorLicenseManager>,
        LicenseManagerBuilder<VendorLicenseManagerBuilder> {
}

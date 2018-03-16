/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.codec.Codec;

/**
 * A context for license management.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContext extends LicenseFactory {

    /** Returns the codec. */
    Codec codec();

    /**
     * Returns a builder for a {@linkplain ConsumerLicenseManager consumer license manager}.
     * Call its {@link ConsumerLicenseManagerBuilder#build} method to build the configured consumer license manager.
     */
    ConsumerLicenseManagerBuilder consumer();

    /** Returns the license management subject. */
    String subject();

    /**
     * Returns a builder for a {@linkplain VendorLicenseManager vendor license manager}.
     * Call its {@link VendorLicenseManagerBuilder#build} method to build the configured vendor license manager.
     */
    VendorLicenseManagerBuilder vendor();
}

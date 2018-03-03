/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.codec.CodecProvider;
import net.truelicense.api.misc.ClassLoaderProvider;

/**
 * A context for license management.
 * Use this context to configure and build a
 * {@linkplain #vendor() vendor license manager} or a
 * {@linkplain #consumer() consumer license manager}.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContext
extends ClassLoaderProvider,
        CodecProvider,
        LicenseFactory,
        LicenseManagementSubjectProvider {

    /**
     * Returns a builder for a
     * {@linkplain ConsumerLicenseManager consumer license manager}.
     * Call its {@link ConsumerLicenseManagerBuilder#build} method to build
     * the configured consumer license manager.
     */
    ConsumerLicenseManagerBuilder consumer();

    /**
     * Returns a builder for a
     * {@linkplain VendorLicenseManager vendor license manager}.
     * Call its {@link VendorLicenseManagerBuilder#build} method to build
     * the configured vendor license manager.
     */
    VendorLicenseManagerBuilder vendor();
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A context for license management.
 */
public interface LicenseManagementContext {

    /**
     * Returns the codec.
     */
    Codec codec();

    /**
     * Returns a builder for a {@linkplain ConsumerLicenseManager consumer license manager}.
     * Call its {@link ConsumerLicenseManagerBuilder#build} method to build the configured consumer license manager.
     */
    ConsumerLicenseManagerBuilder consumer();

    /**
     * Returns the license factory.
     */
    LicenseFactory licenseFactory();

    /**
     * Return the repository factory.
     */
    RepositoryFactory<?> repositoryFactory();

    /**
     * Returns the license management subject.
     */
    String subject();

    /**
     * Returns a builder for a {@linkplain VendorLicenseManager vendor license manager}.
     * Call its {@link VendorLicenseManagerBuilder#build} method to build the configured vendor license manager.
     */
    VendorLicenseManagerBuilder vendor();
}

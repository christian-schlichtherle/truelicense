/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

/**
 * A mix-in for a license manager.
 */
public interface LicenseManagerMixin {

    /**
     * Returns the license manager parameters.
     */
    LicenseManagerParameters parameters();

    /**
     * Returns the license management context from the license manager parameters.
     */
    default LicenseManagementContext context() {
        return parameters().context();
    }
}

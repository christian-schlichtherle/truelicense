/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.api;

/**
 * A fragment of a license manager.
 */
public interface LicenseManagerFragment {

    /**
     * Returns the license management schema.
     */
    LicenseManagementSchema schema();

    /**
     * Returns the license management context from the schema.
     */
    default LicenseManagementContext context() {
        return schema().context();
    }
}

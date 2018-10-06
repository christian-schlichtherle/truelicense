/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.core;

import net.truelicense.api.LicenseManagementContextBuilder;

/**
 * This facade provides a static factory method for a license management context builder with default property values.
 * This class should not be used by applications because the created license management context builders are only
 * partially configured.
 *
 * @author Christian Schlichtherle
 */
public final class Core {

    /**
     * Returns a new license management context builder with default propery values.
     * This method should not be called by applications because the returned license management context builder is only
     * partially configured.
     */
    public static LicenseManagementContextBuilder builder() { return new TrueLicenseManagementContextBuilder(); }

    private Core() { }
}

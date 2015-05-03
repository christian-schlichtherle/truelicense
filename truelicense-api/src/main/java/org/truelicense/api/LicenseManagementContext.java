/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * A root context for the life cycle management of license keys.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContext<PasswordSpecification> {

    /**
     * Returns a context for license consumer applications.
     */
    LicenseConsumerContext<PasswordSpecification> consumer();

    /**
     * Returns a context for license vendor applications alias license key
     * generators.
     */
    LicenseVendorContext<PasswordSpecification> vendor();
}

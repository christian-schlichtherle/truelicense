/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Provides the {@linkplain LicenseManagementContext license management context}
 * and the {@link LicenseManagementParameters license management parameters}.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementSchema {

    /** Returns the license management context from which this license management schema originated. */
    LicenseManagementContext context();

    /** Returns the license management parameters. */
    LicenseManagementParameters parameters();
}

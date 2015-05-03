/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * Creates a new license.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseFactory {

    /** Returns a new license. */
    License license();
}

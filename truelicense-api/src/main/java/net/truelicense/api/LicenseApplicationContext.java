/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * A root context for applications which need to manage license keys.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseApplicationContext {

    /** Returns a new license management context builder. */
    LicenseManagementContextBuilder context();
}

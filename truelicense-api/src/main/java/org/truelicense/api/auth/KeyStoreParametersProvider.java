/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * Provides key store parameters.
 *
 * @author Christian Schlichtherle
 */
public interface KeyStoreParametersProvider {

    /** Returns the key store parameters. */
    KeyStoreParameters parameters();
}

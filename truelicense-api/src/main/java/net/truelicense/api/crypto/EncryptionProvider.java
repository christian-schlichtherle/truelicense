/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

import global.namespace.fun.io.api.Transformation;

/**
 * Provides an encryption.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionProvider {

    /** Returns an encryption transformation. */
    Transformation encryption();
}

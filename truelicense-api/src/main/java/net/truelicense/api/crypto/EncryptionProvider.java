/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

import net.truelicense.api.io.Transformation;

/**
 * Provides an encryption.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionProvider {

    /** Returns an encryption. */
    Transformation encryption();
}

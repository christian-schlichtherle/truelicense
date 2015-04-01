/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.crypto;

/**
 * Provides encryption parameters.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionParametersProvider {
    /** Returns the encryption parameters. */
    EncryptionParameters parameters();
}

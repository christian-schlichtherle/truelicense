/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.passwd;

/**
 * Enumerates the password usages.
 *
 * @author Christian Schlichtherle
 */
public enum PasswordUsage {

    /** The password will be used to decrypt or verify some content. */
    READ,

    /**
     * The password will be used to encrypt or sign some content.
     * This may trigger some additional checks, e.g. compliance to a
     * {@link PasswordPolicy}.
     */
    WRITE
}

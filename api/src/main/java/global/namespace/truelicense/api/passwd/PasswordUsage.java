/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.api.passwd;

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

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.passwd;

/** Enumerates the password usages. */
public enum PasswordUsage {

    /** The password will be used to decrypt or verify some content. */
    DECRYPTION,

    /**
     * The password will be used to encrypt or sign some content.
     * This may trigger some additional checks, e.g. compliance to a
     * {@link PasswordPolicy}.
     */
    ENCRYPTION
}

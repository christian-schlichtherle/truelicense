/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.passwd;

/**
 * Checks password providers for compliance.
 *
 * @author Christian Schlichtherle
 */
public interface PasswordPolicy {

    /**
     * Checks the password provided by the given protection for compliance to
     * this password policy.
     *
     * @throws WeakPasswordException If the password provided by the given
     *         protection does not comply to this password policy.
     */
    void check(PasswordProtection protection) throws Exception;
}

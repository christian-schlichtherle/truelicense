/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.passwd;

/**
 * Checks password providers for compliance.
 *
 * @author Christian Schlichtherle
 */
public interface PasswordPolicy {

    /**
     * Checks the password provided by the given protection for compliance to
     * this policy.
     *
     * @throws WeakPasswordException If the password provided by the given
     *         protection does not comply to this policy.
     */
    void check(PasswordProtection protection) throws Exception;
}

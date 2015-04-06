/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.policy;

/**
 * Checks passwords for compliance.
 *
 * @author Christian Schlichtherle
 */
public interface PasswordPolicy {

    /**
     * Checks the given password for compliance to this policy.
     *
     * @throws WeakPasswordException If the given password does not comply
     *         to this policy.
     */
    void check(char[] passwd) throws WeakPasswordException;
}

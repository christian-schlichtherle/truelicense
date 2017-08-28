/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.passwd;

/**
 * Provides a password policy.
 *
 * @author Christian Schlichtherle
 */
public interface PasswordPolicyProvider {

    /** Returns the password policy. */
    PasswordPolicy policy();
}

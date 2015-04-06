/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.policy;

/**
 * Provides a password policy.
 *
 * @author Christian Schlichtherle
 */
public interface PasswordPolicyProvider {
    /** Returns the password policy. */
    PasswordPolicy policy();
}

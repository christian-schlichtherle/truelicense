/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.passwd;

/**
 * An immutable factory for consistent {@link Password}s.
 *
 * @author Christian Schlichtherle
 */
public interface PasswordProtection {

    /**
     * Returns a new, yet consistent password object.
     *
     * @throws Exception if providing access to the password is not possible.
     */
    Password password(PasswordUsage usage) throws Exception;
}

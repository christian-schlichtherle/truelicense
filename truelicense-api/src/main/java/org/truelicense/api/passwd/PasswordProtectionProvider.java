/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.passwd;

/**
 * A provider for a password protection for a given password specification.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface PasswordProtectionProvider<PasswordSpecification> {

    /** Returns a password protection for the given password specification. */
    PasswordProtection protection(PasswordSpecification specification);
}

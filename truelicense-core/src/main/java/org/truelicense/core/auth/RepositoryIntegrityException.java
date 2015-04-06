/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.auth;

import java.security.GeneralSecurityException;

/**
 * Indicates that the integrity of a {@link org.truelicense.api.auth.Repository}
 * with its encoded artifact has probably been compromised because the public
 * key did not match the private key when verifying the data.
 *
 * @author Christian Schlichtherle
 */
public class RepositoryIntegrityException extends GeneralSecurityException {
    private static final long serialVersionUID = 0L;
}

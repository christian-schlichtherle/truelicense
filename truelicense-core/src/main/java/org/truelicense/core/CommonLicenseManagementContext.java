/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.passwd.PasswordPolicy;
import org.truelicense.api.passwd.PasswordProtection;
import org.truelicense.core.passwd.Passwords;
import org.truelicense.obfuscate.ObfuscatedString;

/**
 * A common license management context.
 * This class uses the class {@link ObfuscatedString} class as the password
 * specification type.
 *
 * @author Christian Schlichtherle
 */
public abstract class CommonLicenseManagementContext
extends BasicLicenseManagementContext<ObfuscatedString> {

    public CommonLicenseManagementContext(String subject) {
        super(subject);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link CommonLicenseManagementContext}
     * returns {@link Passwords#newPasswordPolicy()}.
     */
    @Override
    public PasswordPolicy policy() { return Passwords.newPasswordPolicy(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link CommonLicenseManagementContext}
     * returns
     * {@link Passwords#newPasswordProtection(ObfuscatedString) Passwords.newPasswordProtecetion(os)}.
     */
    @Override
    public PasswordProtection protection(ObfuscatedString os) {
        return Passwords.newPasswordProtection(os);
    }
}

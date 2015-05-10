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
 * A license management context which uses the {@link ObfuscatedString} class
 * as its password specification type.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public abstract class ObfuscatedLicenseManagementContext<Model>
extends TrueLicenseManagementContext<Model, ObfuscatedString> {

    protected ObfuscatedLicenseManagementContext(String subject) {
        super(subject);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link ObfuscatedLicenseManagementContext}
     * returns {@link Passwords#newPasswordPolicy()}.
     */
    @Override
    public PasswordPolicy policy() { return Passwords.newPasswordPolicy(); }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link ObfuscatedLicenseManagementContext}
     * returns
     * {@link Passwords#newPasswordProtection(ObfuscatedString) Passwords.newPasswordProtecetion(os)}.
     */
    @Override
    public PasswordProtection protection(ObfuscatedString os) {
        return Passwords.newPasswordProtection(os);
    }
}

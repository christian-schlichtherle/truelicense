/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.passwd;

import org.truelicense.api.passwd.*;

/**
 * A password policy which ensures that the password is at least eight
 * characters long and consists of both letters and digits.
 *
 * @author Christian Schlichtherle
 */
public final class MinimumPasswordPolicy implements PasswordPolicy {

    @Override
    public void check(final PasswordProtection protection) throws Exception {
        try (Password password = protection.password(PasswordUsage.WRITE)) {
            final char[] passwd = password.characters();
            final int l = passwd.length;
            if (l < 8) throw new WeakPasswordException();
            boolean hasLetter = false, hasDigit = false;
            for (final char c : passwd) {
                if (Character.isLetter(c)) hasLetter = true;
                else if (Character.isDigit(c)) hasDigit = true;
            }
            if (!hasLetter || !hasDigit) throw new WeakPasswordException();
        }
    }
}

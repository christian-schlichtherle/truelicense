/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.policy;

import org.truelicense.api.policy.PasswordPolicy;
import org.truelicense.api.policy.WeakPasswordException;

import javax.annotation.concurrent.Immutable;

/**
 * Checks passwords for compliance.
 * <p>
 * The implementation in the class {@link BasicPasswordPolicy} ensures that the
 * password is at least eight characters long and consists of both letters and
 * digits.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class BasicPasswordPolicy implements PasswordPolicy {

    @Override
    public void check(final char[] passwd) throws WeakPasswordException {
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

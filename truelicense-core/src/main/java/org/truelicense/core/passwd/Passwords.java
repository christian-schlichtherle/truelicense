/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.passwd;

import org.truelicense.api.passwd.*;
import org.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import static java.util.Objects.requireNonNull;

/**
 * A static factory for password management objects.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class Passwords {

    /** Returns a new password protection for the given obfuscated string. */
    public static PasswordProtection newPasswordProtection(final ObfuscatedString os) {
        return newPasswordProvider0(requireNonNull(os));
    }

    private static PasswordProtection newPasswordProvider0(final ObfuscatedString os) {
        requireNonNull(os);
        return new PasswordProtection() {
            @Override public Password password(PasswordUsage usage) {
                return newPassword0(os);
            }
        };
    }

    private static Password newPassword0(final ObfuscatedString os) {
        return new Password() {
            char[] password;

            @Override public char[] characters() {
                if (null == password) password = os.toCharArray();
                return password;
            }

            @Override public void close() {
                if (null != password) Arrays.fill(password, (char) 0);
            }
        };
    }

    /**
     * Returns a new password policy which ensures that the password is at
     * least eight characters long and consists of both letters and digits.
     */
    public static PasswordPolicy newPasswordPolicy() {
        return new PasswordPolicy() {
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
        };
    }

    private Passwords() { }
}

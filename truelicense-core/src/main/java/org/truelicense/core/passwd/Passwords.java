/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.passwd;

import org.truelicense.api.passwd.*;
import org.truelicense.obfuscate.ObfuscatedString;
import org.truelicense.spi.misc.Option;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A static factory for password management objects.
 * This class is trivially immutable.
 *
 * @author Christian Schlichtherle
 */
public final class Passwords {

    /** Returns a new password protection for the given obfuscated string. */
    public static PasswordProtection newPasswordProtection(ObfuscatedString os) {
        return newPasswordProvider0(Objects.requireNonNull(os));
    }

    private static PasswordProtection newPasswordProvider0(final ObfuscatedString os) {
        return new PasswordProtection() {
            @Override public Password password(PasswordUsage usage) {
                return newPassword0(os);
            }
        };
    }

    private static Password newPassword0(final ObfuscatedString os) {
        return new Password() {
            List<char[]> password = Option.none();

            @Override public char[] characters() {
                if (password.isEmpty())
                    password = Option.wrap(os.toCharArray());
                return password.get(0);
            }

            @Override public void close() {
                if (!password.isEmpty())
                    Arrays.fill(password.get(0), (char) 0);
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

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core.passwd;

import net.truelicense.api.passwd.Password;
import net.truelicense.api.passwd.PasswordProtection;
import net.truelicense.api.passwd.PasswordUsage;
import net.truelicense.obfuscate.ObfuscatedString;

import java.util.Arrays;
import java.util.Objects;

/**
 * A password protection which uses an obfuscated string.
 *
 * @author Christian Schlichtherle
 */
public final class ObfuscatedPasswordProtection implements PasswordProtection {

    private final ObfuscatedString os;

    public ObfuscatedPasswordProtection(final ObfuscatedString os) { this.os = Objects.requireNonNull(os); }

    @Override
    public Password password(PasswordUsage usage) { return new ObfuscatedPassword(); }

    private final class ObfuscatedPassword implements Password {

        final char[] characters = os.toCharArray();

        @Override
        public char[] characters() { return characters; }

        @Override
        public void close() { Arrays.fill(characters, (char) 0); }
    }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.util;

import java.util.Locale;
import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;

/**
 * Provides static functions for nullable strings.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class Strings {

    public static boolean equalsIgnoreCase(@CheckForNull String a, @CheckForNull String b) {
        return null == a ? null == b : a.equalsIgnoreCase(b);
    }

    public static String toLowerCase(@CheckForNull String s, Locale l) {
        return null == s ? null : s.toLowerCase(l);
    }

    public static String toUpperCase(@CheckForNull String s, Locale l) {
        return null == s ? null : s.toUpperCase(l);
    }

    private Strings() { }
}

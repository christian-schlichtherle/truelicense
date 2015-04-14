/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.misc;

import java.util.Locale;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Provides static functions for nullable strings.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class Strings {

    public static boolean equalsIgnoreCase(@Nullable String a, @Nullable String b) {
        return null == a ? null == b : a.equalsIgnoreCase(b);
    }

    public static String toLowerCase(@Nullable String s, Locale l) {
        return null == s ? null : s.toLowerCase(l);
    }

    public static String toUpperCase(@Nullable String s, Locale l) {
        return null == s ? null : s.toUpperCase(l);
    }

    private Strings() { }
}

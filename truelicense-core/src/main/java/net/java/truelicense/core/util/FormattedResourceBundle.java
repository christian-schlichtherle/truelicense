/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.util;

import java.text.MessageFormat;
import java.util.*;
import javax.annotation.concurrent.Immutable;
import static net.java.truelicense.core.util.Objects.*;

/**
 * Wraps a {@link ResourceBundle} in order to format strings with
 * {@link MessageFormat#format(String, Object[])}.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class FormattedResourceBundle {

    /**
     * Returns a formatted resource bundle for the given base name.
     *
     * @param baseName the base name of the resource bundle.
     */
    public static FormattedResourceBundle bundle(String baseName) {
        return bundle(baseName, Locale.getDefault());
    }

    /**
     * Returns a formatted resource bundle for the given base name with the
     * given locale.
     *
     * @param baseName the base name of the resource bundle.
     * @param locale the locale to use for the lookup.
     */
    public static FormattedResourceBundle bundle(String baseName, Locale locale) {
        return new FormattedResourceBundle(ResourceBundle.getBundle(baseName, locale));
    }

    /**
     * Wraps the given resource bundle for formatting strings.
     *
     * @param bundle the resource bundle to wrap.
     */
    public static FormattedResourceBundle wrap(ResourceBundle bundle) {
        return new FormattedResourceBundle(requireNonNull(bundle));
    }

    private final ResourceBundle bundle;

    private FormattedResourceBundle(final ResourceBundle bundle) {
        assert null != bundle;
        this.bundle = bundle;
    }

    /**
     * Looks up a string resource identified by {@code key} in this resource
     * bundle and formats it with the given {@code args} using
     * {@link String#format(String, Object[])}.
     */
    public String format(String key, Object... args) {
        return MessageFormat.format(lookup(key), args);
    }

    /**
     * Looks up a string resource identified by {@code key} in this resource
     * bundle.
     */
    public String lookup(String key) { return bundle.getString(key); }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.i18n;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * A formatted internationalized message based on
 * {@link FormattedResourceBundle}.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class FormattedMessage extends BasicMessage {

    private static final long serialVersionUID = 0L;

    private final String baseName;
    private final String key;
    private final Object[] args;

    /**
     * Constructs a formatted message.
     *
     * @param key the key to lookup in the resource bundle.
     * @param args the arguments for formatting the looked up message.
     */
    public FormattedMessage(final Class<?> clazz, final String key, final Object... args) {
        this.baseName = clazz.getName();
        this.key = requireNonNull(key);
        this.args = args.clone();
    }

    /** Returns the base name of the resource bundle to use. */
    protected String baseName() { return baseName; }

    @Override public String toString(Locale locale) {
        final FormattedResourceBundle bundle = bundle(locale);
        return 0 == args.length ? bundle.lookup(key) : bundle.format(key, args);
    }

    private FormattedResourceBundle bundle(Locale locale) {
        return FormattedResourceBundle.bundle(baseName(), locale);
    }
}

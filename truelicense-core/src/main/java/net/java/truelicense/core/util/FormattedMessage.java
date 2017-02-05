/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.util;

import java.util.Locale;
import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;
import static net.java.truelicense.core.util.Objects.*;

/**
 * A formatted internationalized message based on
 * {@link FormattedResourceBundle}.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class FormattedMessage extends BaseMessage {

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
    public FormattedMessage(
            Class<?> clazz,
            String key,
            Object... args) {
        this(clazz.getName(), key, args);
    }

    /**
     * Constructs a formatted message.
     *
     * @param key the key to lookup in the resource bundle.
     * @param args the arguments for formatting the looked up message.
     * @deprecated you must override {@link #baseName()} in the subclass.
     */
    @Deprecated
    protected FormattedMessage(String key, Object... args) {
        this((String) null, key, args);
    }

    private FormattedMessage(
            final @CheckForNull String baseName,
            final String key,
            final Object... args) {
        this.baseName = baseName;
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

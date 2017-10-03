/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.i18n;

import net.truelicense.api.i18n.Message;

import java.util.Locale;

/**
 * A basic message implementation.
 *
 * @author Christian Schlichtherle
 */
public abstract class BasicMessage implements Message {
    @Override
    public final String toString() { return toString(Locale.getDefault()); }
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.spi.i18n;

import global.namespace.truelicense.api.i18n.Message;

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

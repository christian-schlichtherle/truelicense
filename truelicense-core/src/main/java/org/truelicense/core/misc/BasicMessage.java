/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.misc;

import org.truelicense.api.misc.Message;

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

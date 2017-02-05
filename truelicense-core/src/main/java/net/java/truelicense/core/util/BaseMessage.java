/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.util;

import java.util.Locale;

/**
 * A basic message implementation.
 *
 * @author Christian Schlichtherle
 */
public abstract class BaseMessage implements Message {
    @Override
    public final String toString() { return toString(Locale.getDefault()); }
}

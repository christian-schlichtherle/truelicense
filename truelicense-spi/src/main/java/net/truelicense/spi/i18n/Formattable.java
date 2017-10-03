/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.i18n;

import net.truelicense.api.i18n.Message;

/**
 * A formattable object.
 *
 * @author Christian Schlichtherle
 */
public interface Formattable {

    /**
     * Formats the message keyed by this object with the given arguments.
     *
     * @param args the formatting arguments.
     *             Implementations may add constraints to these.
     * @return the formatted message.
     */
    Message format(Object... args);
}

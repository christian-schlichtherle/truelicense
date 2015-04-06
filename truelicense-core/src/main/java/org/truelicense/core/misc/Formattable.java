/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.misc;

import org.truelicense.api.misc.Message;

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

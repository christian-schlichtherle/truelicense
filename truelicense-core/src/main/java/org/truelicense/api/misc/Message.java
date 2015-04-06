/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.misc;

import java.io.Serializable;
import java.util.Locale;

/**
 * An internationalized message.
 *
 * @author Christian Schlichtherle
 */
public interface Message extends Serializable {

    /** Returns a message for the default locale. */
    @Override String toString();

    /** Returns a message for the given locale. */
    String toString(Locale locale);
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.i18n;

import java.io.Serializable;
import java.util.Locale;

/** An internationalized message. */
public interface Message extends Serializable {

    /** Returns a message for the default locale. */
    @Override String toString();

    /** Returns a message for the given locale. */
    String toString(Locale locale);
}

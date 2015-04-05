/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import javax.annotation.concurrent.Immutable;
import org.truelicense.core.util.*;

/**
 * Defines message keys in the resource bundle for this package.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class Messages {

    private static final Class<?> BASE_CLASS = Messages.class;

    static Message message(String key, Object... args) {
        return new FormattedMessage(BASE_CLASS, key, args);
    }

    private Messages() { }
}

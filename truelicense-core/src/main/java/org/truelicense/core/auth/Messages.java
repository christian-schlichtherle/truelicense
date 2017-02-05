/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.auth;

import org.truelicense.api.i18n.Message;
import org.truelicense.spi.i18n.FormattedMessage;

/**
 * Defines message keys in the resource bundle for this package.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class Messages {

    private static final Class<?> BASE_CLASS = Messages.class;

    static Message message(String key, Object... args) {
        return new FormattedMessage(BASE_CLASS, key, args);
    }

    private Messages() { }
}

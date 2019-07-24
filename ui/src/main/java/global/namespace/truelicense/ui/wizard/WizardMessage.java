/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.ui.wizard;

import global.namespace.truelicense.api.i18n.Message;
import global.namespace.truelicense.spi.i18n.Formattable;

/**
 * Enumerates the standard messages of any wizard dialog.
 *
 * @author Christian Schlichtherle
 * @since  TrueLicense 2.3
 */
public enum WizardMessage implements Formattable {

    wizard_back, wizard_next, wizard_cancel, wizard_finish;

    @Override public Message format(Object... args) {
        return Messages.message(name(), args);
    }
}

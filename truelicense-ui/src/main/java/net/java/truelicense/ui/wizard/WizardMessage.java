/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.ui.wizard;

import net.java.truelicense.core.util.*;

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

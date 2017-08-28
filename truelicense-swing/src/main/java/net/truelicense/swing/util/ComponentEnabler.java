/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.swing.util;

import java.awt.*;
import java.io.Serializable;

/**
 * A proxy for enabling or disabling a {@link Component}.
 * This class is immutable.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public abstract class ComponentEnabler implements Enabler, Serializable {

    private static final long serialVersionUID = 0L;

    protected abstract Component component();

    public final void enable() { enabled(true); }
    public final void disable() { enabled(false); }

    @Override public boolean enabled() { return component().isEnabled(); }
    @Override public void enabled(boolean value) { component().setEnabled(value); }
}

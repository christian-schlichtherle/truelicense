/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.swing.util;

import java.awt.*;
import java.io.Serializable;

/**
 * A proxy for enabling or disabling a {@link Component}.
 * This class is immutable.
 */
public abstract class ComponentEnabler implements Enabler, Serializable {

    private static final long serialVersionUID = 0L;

    protected abstract Component component();

    public final void enable() { enabled(true); }
    public final void disable() { enabled(false); }

    @Override public boolean enabled() { return component().isEnabled(); }
    @Override public void enabled(boolean value) { component().setEnabled(value); }
}

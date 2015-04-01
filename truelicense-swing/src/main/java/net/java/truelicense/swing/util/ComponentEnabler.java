package net.java.truelicense.swing.util;

import java.awt.*;
import java.io.Serializable;
import javax.annotation.concurrent.Immutable;

/**
 * A proxy for enabling or disabling a {@link Component}.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@Immutable
public abstract class ComponentEnabler implements Enabler, Serializable {

    private static final long serialVersionUID = 0L;

    protected abstract Component component();

    public final void enable() { enabled(true); }
    public final void disable() { enabled(false); }

    @Override public boolean enabled() { return component().isEnabled(); }
    @Override public void enabled(boolean value) { component().setEnabled(value); }
}

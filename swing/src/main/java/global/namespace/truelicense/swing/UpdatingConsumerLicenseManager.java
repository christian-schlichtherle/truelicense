/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.swing;

import global.namespace.truelicense.api.ConsumerLicenseManager;
import global.namespace.truelicense.swing.util.Enabler;

import javax.swing.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * A decorating consumer license manager which hosts an {@link Enabler}.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
abstract class UpdatingConsumerLicenseManager
extends DecoratingConsumerLicenseManager
implements Serializable {

    private final Enabler enabler;

    UpdatingConsumerLicenseManager(final ConsumerLicenseManager manager, final Enabler enabler) {
        super(manager);
        assert null != enabler;
        this.enabler = enabler;
    }

    final void enable() { enabled(true); }
    final void disable() { enabled(false); }

    final boolean enabled() {
        class Action implements Runnable {
            @SuppressWarnings("WeakerAccess")
            boolean result;

            @Override public void run() { result = enabler.enabled(); }
        }
        return runOnEventDispatchThread(new Action()).result;
    }

    final void enabled(final boolean value) { runOnEventDispatchThread(() -> enabler.enabled(value)); }

    private <R extends Runnable> R runOnEventDispatchThread(R action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(action);
            } catch (InterruptedException ex) {
                action.run(); // never mind!
            } catch (InvocationTargetException ex) {
                throw new AssertionError(ex);
            }
        }
        return action;
    }
}

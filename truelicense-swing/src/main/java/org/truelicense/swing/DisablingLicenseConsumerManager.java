/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import org.truelicense.api.LicenseConsumerManager;
import org.truelicense.api.LicenseManagementException;
import org.truelicense.api.io.Source;
import org.truelicense.swing.util.Enabler;

/**
 * A decorating license consumer manager which disables a component before it
 * forwards the call to {@link #install} or {@link #uninstall} to the delegate
 * manager.
 * If the operation succeeds, the component remains disabled.
 * Otherwise, the component state gets recovered.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class DisablingLicenseConsumerManager
extends UpdatingLicenseConsumerManager {

    private static final long serialVersionUID = 0L;

    DisablingLicenseConsumerManager(
            Enabler enabler,
            LicenseConsumerManager manager) {
        super(manager, enabler);
    }

    @Override
    public void install(final Source source)
    throws LicenseManagementException {
        run(new Action<Void>() {
            @Override public Void call() throws LicenseManagementException {
                manager.install(source);
                return null;
            }
        });
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        run(new Action<Void>() {
            @Override public Void call() throws LicenseManagementException {
                manager.uninstall();
                return null;
            }
        });
    }

    private <T> T run(final Action<T> action)
    throws LicenseManagementException {
        final boolean enabled = enabled();
        disable();
        try {
            return action.call();
        } catch (LicenseManagementException | RuntimeException | Error e) {
            enabled(enabled);
            throw e;
        }
    }

    private interface Action<T> { T call() throws LicenseManagementException; }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.swing;

import javax.annotation.concurrent.Immutable;
import net.java.truelicense.core.License;
import net.java.truelicense.core.LicenseConsumerManager;
import net.java.truelicense.core.LicenseManagementException;
import net.java.truelicense.core.io.Source;
import net.java.truelicense.swing.util.Enabler;

/**
 * A decorating license consumer manager which disables a component before it
 * forwards the call to {@link #install} or {@link #uninstall} to the delegate
 * manager.
 * If the operation succeeds, the component remains disabled.
 * Otherwise, the component state gets recovered.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class DisablingLicenseConsumerManager
extends UpdatingLicenseConsumerManager {

    private static final long serialVersionUID = 0L;

    DisablingLicenseConsumerManager(
            Enabler enabler,
            LicenseConsumerManager manager) {
        super(manager, enabler);
    }

    @Override
    public License install(final Source source)
    throws LicenseManagementException {
        return run(new Action<License>() {
            @Override public License call() throws LicenseManagementException {
                return manager.install(source);
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
        } catch (final LicenseManagementException ex) {
            enabled(enabled);
            throw ex;
        } catch (final RuntimeException ex) {
            enabled(enabled);
            throw ex;
        } catch (final Error ex) {
            enabled(enabled);
            throw ex;
        }
    }

    private interface Action<T> { T call() throws LicenseManagementException; }
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.swing;

import global.namespace.fun.io.api.Source;
import global.namespace.truelicense.api.LicenseManagementException;
import global.namespace.truelicense.swing.util.Enabler;
import global.namespace.truelicense.api.ConsumerLicenseManager;

/**
 * A decorating consumer license manager which disables a component before it
 * forwards the call to {@link ConsumerLicenseManager#install} or {@link #uninstall} to the delegate
 * manager.
 * If the operation succeeds, the component remains disabled.
 * Otherwise, the component state gets recovered.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class DisablingConsumerLicenseManager extends UpdatingConsumerLicenseManager {

    private static final long serialVersionUID = 0L;

    DisablingConsumerLicenseManager(Enabler enabler, ConsumerLicenseManager manager) { super(manager, enabler); }

    @Override
    public void install(Source source) throws LicenseManagementException { run(() -> manager.install(source)); }

    @Override
    public void uninstall() throws LicenseManagementException { run(manager::uninstall); }

    private void run(final CheckedTask task) throws LicenseManagementException {
        final boolean enabled = enabled();
        disable();
        try {
            task.run();
        } catch (final Throwable e) {
            enabled(enabled);
            throw e;
        }
    }

    private interface CheckedTask { void run() throws LicenseManagementException; }
}

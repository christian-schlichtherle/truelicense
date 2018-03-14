/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.swing;

import global.namespace.fun.io.api.Source;
import net.truelicense.api.ConsumerLicenseManager;
import net.truelicense.api.LicenseManagementException;
import net.truelicense.swing.util.Enabler;

/**
 * A decorating consumer license manager which enables a component after it has
 * successfully called {@link ConsumerLicenseManager#install} or {@link #uninstall} on the delegate
 * manager.
 * If the operation fails, the component's state remains unchanged.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class EnablingConsumerLicenseManager
extends UpdatingConsumerLicenseManager {

    private static final long serialVersionUID = 0L;

    EnablingConsumerLicenseManager(
            Enabler enabler,
            ConsumerLicenseManager manager) {
        super(manager, enabler);
    }

    @Override
    public void install(final Source source) throws LicenseManagementException {
        manager.install(source);
        enable();
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        manager.uninstall();
        enable();
    }
}

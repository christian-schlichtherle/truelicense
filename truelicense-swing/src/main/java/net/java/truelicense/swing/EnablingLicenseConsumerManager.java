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
 * A decorating license consumer manager which enables a component after it has
 * successfully called {@link #install} or {@link #uninstall} on the delegate
 * manager.
 * If the operation fails, the component's state remains unchanged.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class EnablingLicenseConsumerManager
extends UpdatingLicenseConsumerManager {

    private static final long serialVersionUID = 0L;

    EnablingLicenseConsumerManager(
            Enabler enabler,
            LicenseConsumerManager manager) {
        super(manager, enabler);
    }

    @Override
    public License install(final Source source) throws LicenseManagementException {
        final License license = manager.install(source);
        enable();
        return license;
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        manager.uninstall();
        enable();
    }
}

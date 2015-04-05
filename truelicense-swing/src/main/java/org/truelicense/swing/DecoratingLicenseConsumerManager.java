/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import org.truelicense.core.*;
import org.truelicense.core.io.Source;
import org.truelicense.core.util.Objects;

/**
 * A decorator for a license consumer manager.
 *
 * @author Christian Schlichtherle
 */
abstract class DecoratingLicenseConsumerManager
implements LicenseConsumerManager {

    @SuppressWarnings({"PackageVisibleField"})
    protected LicenseConsumerManager manager;

    protected DecoratingLicenseConsumerManager() { }

    protected DecoratingLicenseConsumerManager(final LicenseConsumerManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @Override public String subject() { return manager.subject(); }

    @Override public LicenseParameters parameters() {
        return manager.parameters();
    }

    @Override public LicenseConsumerContext context() {
        return manager.context();
    }

    @Override
    public License install(Source source) throws LicenseManagementException {
        return manager.install(source);
    }

    @Override public License view() throws LicenseManagementException {
        return manager.view();
    }

    @Override public void verify() throws LicenseManagementException {
        manager.verify();
    }

    @Override public void uninstall() throws LicenseManagementException {
        manager.uninstall();
    }
}

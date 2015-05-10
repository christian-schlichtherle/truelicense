/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import org.truelicense.api.*;
import org.truelicense.api.io.Source;

import java.util.Objects;

/**
 * A decorator for a consumer license manager.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
abstract class DecoratingConsumerLicenseManager
implements ConsumerLicenseManager {

    @SuppressWarnings({"PackageVisibleField"})
    protected final ConsumerLicenseManager manager;

    protected DecoratingConsumerLicenseManager(final ConsumerLicenseManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @Override
    public LicenseManagementContext context() {
        return manager.context();
    }

    @Override
    public LicenseManagementParameters parameters() {
        return manager.parameters();
    }

    @Override
    public void install(Source source) throws LicenseManagementException {
        manager.install(source);
    }

    @Override
    public License view() throws LicenseManagementException {
        return manager.view();
    }

    @Override
    public void verify() throws LicenseManagementException {
        manager.verify();
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        manager.uninstall();
    }
}

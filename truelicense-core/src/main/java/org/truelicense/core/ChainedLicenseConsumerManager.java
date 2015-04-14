/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.License;
import org.truelicense.api.LicenseConsumerManager;
import org.truelicense.api.LicenseManagementException;
import org.truelicense.api.LicenseProvider;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.spi.misc.Option;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * A caching license consumer manager which establishes a Chain Of
 * Responsibility with its parent license consumer manager.
 * On each operation, the parent license consumer manager is tried first.
 *
 * @author Christian Schlichtherle
 */
@ThreadSafe
abstract class ChainedLicenseConsumerManager
extends CachingLicenseConsumerManager implements LicenseProvider {

    private volatile List<Boolean> canCreateLicenseKeys = Option.none();

    /** The parent license consumer manager. */
    abstract LicenseConsumerManager parent();

    private boolean canCreateLicenseKeys() {
        if (canCreateLicenseKeys.isEmpty()) {
            synchronized (this) {
                if (canCreateLicenseKeys.isEmpty()) {
                    try {
                        super.create(license(), bios().memoryStore()); // -> /dev/null
                        canCreateLicenseKeys = Option.wrap(Boolean.TRUE);
                    } catch (LicenseManagementException ignored) {
                        canCreateLicenseKeys = Option.wrap(Boolean.FALSE);
                    }
                }
            }
        }
        return canCreateLicenseKeys.get(0);
    }

    @Override
    public void install(Source source) throws LicenseManagementException {
        try {
            parent().install(source);
        } catch (final LicenseManagementException primary) {
            if (canCreateLicenseKeys()) throw primary;
            super.install(source);
        }
    }

    @Override public License view() throws LicenseManagementException {
        try {
            return parent().view();
        } catch (final LicenseManagementException primary) {
            try {
                return super.view(); // uses store()
            } catch (final LicenseManagementException secondary) {
                synchronized (store()) {
                    try {
                        return super.view(); // repeat
                    } catch (final LicenseManagementException ternary) {
                        return createIffNewFtp(ternary); // uses store(), too
                    }
                }
            }
        }
    }

    @Override public void verify() throws LicenseManagementException {
        try {
            parent().verify();
        } catch (final LicenseManagementException primary) {
            try {
                super.verify(); // uses store()
            } catch (final LicenseManagementException secondary) {
                synchronized (store()) {
                    try {
                        super.verify(); // repeat
                    } catch (final LicenseManagementException ternary) {
                        createIffNewFtp(ternary); // uses store(), too
                    }
                }
            }
        }
    }

    @Override public void uninstall() throws LicenseManagementException {
        try {
            parent().uninstall();
        } catch (final LicenseManagementException primary) {
            if (canCreateLicenseKeys()) throw primary;
            super.uninstall();
        }
    }

    private License createIffNewFtp(final LicenseManagementException ex)
    throws LicenseManagementException {
        if (!canCreateLicenseKeys()) throw ex;
        final Store store = store();
        if (store.exists()) throw ex;
        return super.create(license(), store);
    }
}

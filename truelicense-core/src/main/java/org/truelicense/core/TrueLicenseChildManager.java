/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.spi.misc.Option;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * A caching license consumer manager which establishes a Chain Of
 * Responsibility with its parent license consumer manager.
 * On each operation, the parent license consumer manager is tried first.
 * This class is thread-safe.
 *
 * @author Christian Schlichtherle
 */
class TrueLicenseChildManager<Model>
extends TrueLicenseCachingManager<Model> {

    private volatile List<Boolean> canGenerateLicenseKeys = Option.none();

    TrueLicenseChildManager(
            TrueLicenseApplicationContext<Model, ?>.TrueLicenseParameters parameters) {
        super(parameters);
    }

    @Override
    public void install(Source source) throws LicenseManagementException {
        try {
            parent().install(source);
        } catch (final LicenseManagementException primary) {
            if (canGenerateLicenseKeys()) throw primary;
            super.install(source);
        }
    }

    @Override
    public License view() throws LicenseManagementException {
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
                        return generateIffNewFtp(ternary).license(); // uses store(), too
                    }
                }
            }
        }
    }

    @Override
    public void verify() throws LicenseManagementException {
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
                        generateIffNewFtp(ternary); // uses store(), too
                    }
                }
            }
        }
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        try {
            parent().uninstall();
        } catch (final LicenseManagementException primary) {
            if (canGenerateLicenseKeys()) throw primary;
            super.uninstall();
        }
    }

    private boolean canGenerateLicenseKeys() {
        if (canGenerateLicenseKeys.isEmpty()) {
            synchronized (this) {
                if (canGenerateLicenseKeys.isEmpty()) {
                    try {
                        // Test encoding a new license key to /dev/null .
                        super.generator(license()).writeTo(bios().memoryStore());
                        canGenerateLicenseKeys = Option.wrap(Boolean.TRUE);
                    } catch (LicenseManagementException ignored) {
                        canGenerateLicenseKeys = Option.wrap(Boolean.FALSE);
                    }
                }
            }
        }
        return canGenerateLicenseKeys.get(0);
    }

    private static boolean exists(final Store store) throws LicenseManagementException {
        return wrap(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return store.exists();
            }
        });
    }

    private LicenseKeyGenerator generateIffNewFtp(final LicenseManagementException e)
            throws LicenseManagementException {
        if (!canGenerateLicenseKeys())
            throw e;
        final Store store = store();
        if (exists(store))
            throw e;
        return super.generator(license()).writeTo(store);
    }

    final License license() { return parameters().license(); }

    final LicenseConsumerManager parent() { return parameters().parent(); }
}

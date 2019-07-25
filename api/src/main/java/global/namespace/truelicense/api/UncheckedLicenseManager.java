/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import java.util.concurrent.Callable;

/**
 * Provides utilities for unchecked license managers.
 *
 * @see UncheckedConsumerLicenseManager
 * @see UncheckedVendorLicenseManager
 */
final class UncheckedLicenseManager {

    private UncheckedLicenseManager() {
    }

    static <V> V callUnchecked(Callable<V> task) {
        try {
            return task.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UncheckedLicenseManagementException(e);
        }
    }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * @author Christian Schlichtherle
 */
public interface UncheckedVendorLicenseManager extends VendorLicenseManager {

    @Override
    UncheckedLicenseKeyGenerator generator(License bean) throws UncheckedLicenseManagementException;
}

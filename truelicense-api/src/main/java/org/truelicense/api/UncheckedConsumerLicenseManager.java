/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Source;

/**
 * @author Christian Schlichtherle
 */
public interface UncheckedConsumerLicenseManager extends ConsumerLicenseManager {

    @Override
    void install(Source source) throws LicenseManagementRuntimeException;

    @Override
    License view() throws LicenseManagementRuntimeException;

    @Override
    void verify() throws LicenseManagementRuntimeException;

    @Override
    void uninstall() throws LicenseManagementRuntimeException;
}

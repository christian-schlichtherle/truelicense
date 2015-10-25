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
    void install(Source source) throws UncheckedLicenseManagementException;

    @Override
    License view() throws UncheckedLicenseManagementException;

    @Override
    void verify() throws UncheckedLicenseManagementException;

    @Override
    void uninstall() throws UncheckedLicenseManagementException;
}

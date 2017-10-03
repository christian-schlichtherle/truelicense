/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.io.Source;

/**
 * A consumer license manager which may generally throw an {@link UncheckedLicenseManagementException} rather than a
 * (checked) {@link LicenseManagementException}.
 *
 * @see UncheckedLicenseManager#from(ConsumerLicenseManager)
 * @author Christian Schlichtherle
 */
public interface UncheckedConsumerLicenseManager extends ConsumerLicenseManager {

    /** Returns the underlying (checked) consumer license manager. */
    ConsumerLicenseManager checked();

    @Override
    void install(Source source) throws UncheckedLicenseManagementException;

    @Override
    License load() throws UncheckedLicenseManagementException;

    @Override
    void verify() throws UncheckedLicenseManagementException;

    @Override
    void uninstall() throws UncheckedLicenseManagementException;
}

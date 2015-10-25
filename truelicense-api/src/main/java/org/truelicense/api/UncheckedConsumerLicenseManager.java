/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Source;

/**
 * A consumer license manager which may generally throw an
 * {@link UncheckedLicenseManagementException} rather than a (checked)
 * {@link LicenseManagementException}.
 *
 * @see UncheckedManager#from(ConsumerLicenseManager)
 * @author Christian Schlichtherle
 */
public interface UncheckedConsumerLicenseManager extends ConsumerLicenseManager {

    /** Returns the underlying (checked) consumer license manager. */
    ConsumerLicenseManager checked();

    @Override
    void install(Source source) throws UncheckedLicenseManagementException;

    @Override
    License view() throws UncheckedLicenseManagementException;

    @Override
    void verify() throws UncheckedLicenseManagementException;

    @Override
    void uninstall() throws UncheckedLicenseManagementException;
}

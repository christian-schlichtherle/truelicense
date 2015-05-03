/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.LicenseAuthorization;
import org.truelicense.api.LicenseParameters;

/**
 * A basic license authorization which authorizes all operations.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class TrueLicenseAuthorization implements LicenseAuthorization {
    @Override public void clearGenerator(LicenseParameters parameters) { }
    @Override public void clearInstall(LicenseParameters parameters) { }
    @Override public void clearVerify(LicenseParameters parameters) { }
    @Override public void clearView(LicenseParameters parameters) { }
    @Override public void clearUninstall(LicenseParameters parameters) { }
}

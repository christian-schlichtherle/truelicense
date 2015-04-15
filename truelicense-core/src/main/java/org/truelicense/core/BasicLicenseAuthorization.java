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
final class BasicLicenseAuthorization implements LicenseAuthorization {
    @Override public void clearCreate(LicenseParameters lp) { }
    @Override public void clearInstall(LicenseParameters lp) { }
    @Override public void clearVerify(LicenseParameters lp) { }
    @Override public void clearView(LicenseParameters lp) { }
    @Override public void clearUninstall(LicenseParameters lp) { }
}

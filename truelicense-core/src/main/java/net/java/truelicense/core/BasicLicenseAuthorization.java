/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import javax.annotation.concurrent.Immutable;

/**
 * A basic license authorization which authorizes all operations.
 *
 * @author Christian Schlichtherle
 */
@Immutable
final class BasicLicenseAuthorization implements LicenseAuthorization {
    @Override public void clearCreate(LicenseParameters lp) { }
    @Override public void clearInstall(LicenseParameters lp) { }
    @Override public void clearVerify(LicenseParameters lp) { }
    @Override public void clearView(LicenseParameters lp) { }
    @Override public void clearUninstall(LicenseParameters lp) { }
}

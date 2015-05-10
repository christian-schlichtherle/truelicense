/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.LicenseAuthorization;
import org.truelicense.api.LicenseManagementSchema;

/**
 * A basic license authorization which authorizes all operations.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class TrueLicenseAuthorization implements LicenseAuthorization {
    @Override public void clearGenerator(LicenseManagementSchema schema) { }
    @Override public void clearInstall(LicenseManagementSchema schema) { }
    @Override public void clearVerify(LicenseManagementSchema schema) { }
    @Override public void clearView(LicenseManagementSchema schema) { }
    @Override public void clearUninstall(LicenseManagementSchema schema) { }
}

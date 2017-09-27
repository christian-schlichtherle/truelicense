/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core;

import net.truelicense.api.LicenseManagementAuthorization;
import net.truelicense.api.LicenseManagementSchema;

/**
 * A basic license authorization which authorizes all operations.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public final class TrueLicenseManagementAuthorization implements LicenseManagementAuthorization {

    @Override public void clearGenerate(LicenseManagementSchema schema) { }
    @Override public void clearInstall(LicenseManagementSchema schema) { }
    @Override public void clearLoad(LicenseManagementSchema schema) { }
    @Override public void clearVerify(LicenseManagementSchema schema) { }
    @Override public void clearUninstall(LicenseManagementSchema schema) { }
}

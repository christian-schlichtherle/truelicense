/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.jsf;

import javax.faces.component.FacesComponent;
import net.truelicense.api.LicenseManagementException;
import static net.truelicense.ui.LicenseWizardMessage.*;
import net.truelicense.ui.LicenseWizardState;

/**
 * The backing bean for uninstalling the installed license key.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@FacesComponent
public final class UninstallBean extends LicenseBean {

    public String getTitle() { return message(uninstall_title); }
    public String getPrompt() { return message(uninstall_prompt); }
    public String getSuccess() { return message(uninstall_success); }
    public String getFailure() { return message(uninstall_failure); }

    public String getUninstallValue() { return message(uninstall_uninstall); }
    public boolean isUninstallDisabled() { return !licenseInstalled(); }

    public String uninstallAction() {
        try {
            manager().uninstall();
            outputInfo(getSuccess());
        } catch (LicenseManagementException ex) {
            outputError(getFailure(), ex);
        }
        return null;
    }

    @Override public LicenseWizardState nextState() {
        return LicenseWizardState.uninstall;
    }
}

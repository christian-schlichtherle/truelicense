/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.jsf;

import javax.faces.component.FacesComponent;
import static org.truelicense.ui.LicenseWizardMessage.*;
import org.truelicense.ui.LicenseWizardState;

/**
 * The backing bean for welcoming the user and displaying available options.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@FacesComponent
public final class WelcomeBean extends LicenseBean {

    private LicenseWizardState nextState;

    public String getTitle() { return message(welcome_title); }

    public String getPrompt() { return message(welcome_prompt); }

    public String getInstallLabel() { return message(welcome_install); }
    public LicenseWizardState getInstallValue() { return LicenseWizardState.install; }

    public String getDisplayLabel() { return message(welcome_display); }
    public LicenseWizardState getDisplayValue() { return LicenseWizardState.display; }
    public boolean isDisplayDisabled() { return isUninstallDisabled(); }

    public String getUninstallLabel() { return message(welcome_uninstall); }
    public LicenseWizardState getUninstallValue() { return LicenseWizardState.uninstall; }
    public boolean isUninstallDisabled() { return !licenseInstalled(); }

    @Override public LicenseWizardState nextState() {
        return getNextState();
    }

    /**
     * Returns the next state.
     * The default value is determined as follows:
     * <ul>
     * <li>If a valid license key is installed, the next state is {@code display}.
     * <li>Otherwise, the next state is {@code install}.
     * </ul>
     * <p>
     * Note that this property is not persisted in the state helper.
     */
    public LicenseWizardState getNextState() {
        final LicenseWizardState nextState = this.nextState;
        return null != nextState ? nextState : (this.nextState = super.nextState());
    }

    public void setNextState(final LicenseWizardState nextState) {
        this.nextState = nextState;
    }
}

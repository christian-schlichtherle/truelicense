/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import org.truelicense.api.ConsumerLicenseManager;
import org.truelicense.api.LicenseManagementContext;
import org.truelicense.api.LicenseManagementException;
import org.truelicense.ui.LicenseWizardMessage;
import org.truelicense.ui.LicenseWizardState;
import org.truelicense.ui.wizard.WizardView;

import javax.swing.*;
import java.util.Objects;

/**
 * Defines common properties of license panels.
 *
 * @author Christian Schlichtherle
 */
abstract class LicensePanel
extends JPanel
implements WizardView<LicenseWizardState> {

    private final LicenseManagementWizard wizard;

    LicensePanel(final LicenseManagementWizard wizard) {
        this.wizard = Objects.requireNonNull(wizard);
    }

    final LicenseManagementWizard wizard() { return wizard; }

    ConsumerLicenseManager manager() { return wizard().manager(); }

    final String subject() { return context().subject(); }

    private LicenseManagementContext context() { return manager().context(); }

    final String format(LicenseWizardMessage key) {
        return key.format(subject()).toString();
    }

    @Override public LicenseWizardState backState() {
        return LicenseWizardState.welcome;
    }

    @Override public LicenseWizardState nextState() {
        try {
            manager().verify();
            return LicenseWizardState.display;
        } catch (LicenseManagementException ex) {
            return LicenseWizardState.install;
        }
    }

    final boolean licenseInstalled() {
        try {
            manager().view();
            return true;
        } catch (LicenseManagementException ex) {
            return false;
        }
    }

    @Override public void onBeforeStateSwitch() { }
    @Override public void onAfterStateSwitch() { }
}

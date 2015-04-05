/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import org.truelicense.core.LicenseConsumerManager;
import org.truelicense.core.LicenseManagementException;
import org.truelicense.core.util.Objects;
import org.truelicense.ui.LicenseWizardMessage;
import org.truelicense.ui.LicenseWizardState;
import org.truelicense.ui.wizard.WizardView;

import javax.swing.*;

/**
 * Defines common properties of license panels.
 *
 * @author Christian Schlichtherle
 */
abstract class LicensePanel
extends JPanel
implements WizardView<LicenseWizardState> {

    private final LicenseWizard wizard;

    LicensePanel(final LicenseWizard wizard) {
        this.wizard = Objects.requireNonNull(wizard);
    }

    final LicenseWizard wizard() { return wizard; }

    LicenseConsumerManager manager() { return wizard().manager(); }

    final String subject() { return manager().subject(); }

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

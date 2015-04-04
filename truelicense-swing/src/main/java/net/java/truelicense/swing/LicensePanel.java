/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.swing;

import net.java.truelicense.core.LicenseConsumerManager;
import net.java.truelicense.core.LicenseManagementException;
import net.java.truelicense.core.util.Objects;
import net.java.truelicense.ui.LicenseWizardMessage;
import net.java.truelicense.ui.LicenseWizardState;
import net.java.truelicense.ui.wizard.WizardView;

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

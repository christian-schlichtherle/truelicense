/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import org.truelicense.api.ConsumerLicenseManager;
import org.truelicense.swing.util.ComponentEnabler;
import org.truelicense.swing.util.EnhancedDialog;
import org.truelicense.swing.wizard.SwingWizardController;
import org.truelicense.ui.LicenseWizardMessage;
import org.truelicense.ui.LicenseWizardState;
import org.truelicense.ui.wizard.BasicWizardModel;
import org.truelicense.ui.wizard.WizardModel;

import java.awt.*;

import static org.truelicense.ui.LicenseWizardState.*;

/**
 * An internationalized wizard dialog for license management in consumer
 * applications.
 *
 * @author Christian Schlichtherle
 */
public final class LicenseManagementWizard {

    /** Indicates that the "Finish" component was pressed to close the dialog. */
    public static final int FINISH_RETURN_CODE =
            SwingWizardController.ReturnCode.finish.ordinal();

    /** Indicates that the "Cancel" component was pressed to close the dialog. */
    public static final int CANCEL_RETURN_CODE =
            SwingWizardController.ReturnCode.cancel.ordinal();

    private final LicenseWizardController controller;
    private final ConsumerLicenseManager manager;

    /**
     * Constructs a license wizard.
     *
     * @param manager the consumer license manager.
     */
    public LicenseManagementWizard(ConsumerLicenseManager manager) {
        this(manager, (Frame) null);
    }

    /**
     * Constructs a license wizard for the given owner dialog.
     *
     * @param manager the consumer license manager.
     * @param owner the owner dialog.
     */
    public LicenseManagementWizard(
            ConsumerLicenseManager manager,
            Dialog owner) {
        this(new EnhancedDialog(owner), manager);
    }

    /**
     * Constructs a license wizard for the given owner frame.
     *
     * @param manager the consumer license manager.
     * @param owner the owner frame.
     */
    public LicenseManagementWizard(
            ConsumerLicenseManager manager,
            Frame owner) {
        this(new EnhancedDialog(owner), manager);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    private LicenseManagementWizard(
            final EnhancedDialog dialog,
            final ConsumerLicenseManager manager) {
        dialog.setTitle(LicenseWizardMessage.wizard_title
                .format(manager.context().subject()).toString());
        this.manager = manager;
        this.controller = new LicenseWizardController(dialog);
        this.controller.setupPanels(this);
    }

    void enableNextButton() { nextButtonProxy().enable(); }
    void disableNextButton() { nextButtonProxy().disable(); }

    ComponentEnabler nextButtonProxy() { return controller.nextButtonProxy(); }

    ConsumerLicenseManager manager() { return manager; }

    /**
     * Returns {@code true} if and only if the uninstall-button is visible on
     * the welcome panel.
     * By default, this button is <em>not</em> visible.
     */
    public boolean isUninstallButtonVisible() {
        return controller.isUninstallButtonVisible();
    }

    /** Sets the visibility of the uninstall-button. */
    public void setUninstallButtonVisible(boolean visible) {
        controller.setUninstallButtonVisible(visible);
    }

    /**
     * Packs and displays the modal wizard dialog.
     *
     * @return An integer that identifies how the dialog was closed.
     *         See return code constants at the beginning of this class.
     */
    public int showModalDialog() {
        return controller.showModalDialog().ordinal();
    }

    /**
     * Retrieves the last return code set by the dialog.
     *
     * @return An integer that identifies how the dialog was closed.
     *         See RETURN_CODE constants at the beginning of this class.
     */
    public int getReturnCode() { return controller.lastReturnCode().ordinal(); }

    private static final class LicenseWizardController
    extends SwingWizardController<LicenseWizardState, LicensePanel> {

        private final WizardModel<LicenseWizardState, LicensePanel>
                model = BasicWizardModel.create(LicenseWizardState.class);

        LicenseWizardController(EnhancedDialog dialog) { super(dialog); }

        @Override protected WizardModel<LicenseWizardState, LicensePanel> model() {
            return model;
        }

        @Override protected ComponentEnabler nextButtonProxy() {
            return super.nextButtonProxy();
        }

        void setupPanels(final LicenseManagementWizard wizard) {
            view(welcome, new WelcomePanel(wizard));
            view(install, new InstallPanel(wizard));
            view(display, new DisplayPanel(wizard));
            view(uninstall, new UninstallPanel(wizard));
        }

        boolean isUninstallButtonVisible() {
            return welcomePanel().isUninstallButtonVisible();
        }

        void setUninstallButtonVisible(boolean visible) {
            welcomePanel().setUninstallButtonVisible(visible);
        }

        private WelcomePanel welcomePanel() {
            return (WelcomePanel) super.view(LicenseWizardState.welcome);
        }
    }
}

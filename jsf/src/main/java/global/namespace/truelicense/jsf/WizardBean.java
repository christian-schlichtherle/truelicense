/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.jsf;

import global.namespace.truelicense.ui.LicenseWizardState;
import global.namespace.truelicense.ui.wizard.BasicWizardController;
import global.namespace.truelicense.ui.wizard.WizardController;
import global.namespace.truelicense.ui.wizard.WizardModel;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import static global.namespace.truelicense.ui.LicenseWizardMessage.wizard_title;
import static global.namespace.truelicense.ui.wizard.WizardMessage.wizard_back;
import static global.namespace.truelicense.ui.wizard.WizardMessage.wizard_next;

/**
 * The backing bean for the wizard dialog for licese consumer management.
 */
@FacesComponent
public final class WizardBean
extends LicenseBean
implements WizardModel<LicenseWizardState, LicenseBean> {

    private final Map<LicenseWizardState, LicenseBean>
            beans = new EnumMap<LicenseWizardState, LicenseBean>(LicenseWizardState.class);

    private final WizardController
            controller = BasicWizardController.create(this);

    @Override public LicenseBean view(LicenseWizardState state) {
        return beans.get(state);
    }

    @Override public void view(LicenseWizardState state, LicenseBean view) {
        beans.put(state, Objects.requireNonNull(view));
    }

    @Override public LicenseWizardState currentState() {
        final LicenseWizardState currentState =
                (LicenseWizardState) getStateHelper().get("currentState");
        return null != currentState ? currentState : LicenseWizardState.welcome;
    }

    @Override public void currentState(LicenseWizardState state) {
        getStateHelper().put("currentState", state);
    }

    public LicenseBean getWelcomeBean() {
        return view(LicenseWizardState.welcome);
    }

    public void setWelcomeBean(@NotNull LicenseBean panel) {
        view(LicenseWizardState.welcome, panel);
    }

    public LicenseBean getInstallBean() {
        return view(LicenseWizardState.install);
    }

    public void setInstallBean(@NotNull LicenseBean panel) {
        view(LicenseWizardState.install, panel);
    }

    public LicenseBean getDisplayBean() {
        return view(LicenseWizardState.display);
    }

    public void setDisplayBean(@NotNull LicenseBean panel) {
        view(LicenseWizardState.display, panel);
    }

    public LicenseBean getUninstallBean() {
        return view(LicenseWizardState.uninstall);
    }

    public void setUninstallBean(@NotNull LicenseBean panel) {
        view(LicenseWizardState.uninstall, panel);
    }

    public String getTitle() { return message(wizard_title); }

    public String getSwitchBackLabel() { return message(wizard_back); }

    public boolean isSwitchBackDisabled() {
        return !controller.switchBackEnabled();
    }

    public String switchBackAction() {
        controller.switchBack();
        return null;
    }

    public String getSwitchNextLabel() { return message(wizard_next); }

    public boolean isSwitchNextDisabled() {
        return !controller.switchNextEnabled();
    }

    public String switchNextAction() {
        controller.switchNext();
        return null;
    }

    @Override
    public void encodeBegin(final FacesContext context) throws IOException {
        final LicenseWizardState currentState = currentState();
        for (LicenseWizardState state : LicenseWizardState.values())
            view(state).setRendered(currentState == state);
        super.encodeBegin(context);
    }
}

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.jsf;

import global.namespace.truelicense.api.ConsumerLicenseManager;
import global.namespace.truelicense.api.LicenseManagementException;
import global.namespace.truelicense.api.LicenseManagerParameters;
import global.namespace.truelicense.spi.i18n.Formattable;
import global.namespace.truelicense.ui.LicenseWizardState;
import global.namespace.truelicense.ui.misc.MnemonicText;
import global.namespace.truelicense.ui.wizard.WizardView;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * A base class for backing beans for license consumer management.
 */
public abstract class LicenseBean
extends UINamingContainer
implements WizardView<LicenseWizardState> {

    private ConsumerLicenseManager manager;

    LicenseBean() { }

    final ConsumerLicenseManager manager() {
        final ConsumerLicenseManager m = manager;
        return null != m ? m : (manager = resolveManager());
    }

    private ConsumerLicenseManager resolveManager() {
        final Object manager = getAttributes().get("manager");
        if (!(manager instanceof ConsumerLicenseManager))
            throw new IllegalStateException("Missing consumer license manager in attributes map.");
        return (ConsumerLicenseManager) manager;
    }

    final String message(Formattable key) { return message(key, subject()); }

    final String message(Formattable key, Object... args) {
        return new MnemonicText(key.format(args).toString(locale())).getText();
    }

    final String subject() {
        return parameters().subject();
    }

    final LicenseManagerParameters parameters() {
        return manager().parameters();
    }

    private Locale locale() { return viewRoot().getLocale(); }

    private ExternalContext externalContext() {
        return facesContext().getExternalContext();
    }

    private UIViewRoot viewRoot() { return facesContext().getViewRoot(); }

    private FacesContext facesContext() {
        return FacesContext.getCurrentInstance();
    }

    final void outputInfo(String summary) {
        outputInfo(summary, "");
    }

    final void outputInfo(String summary, String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        if (summary.equals(detail) || detail.isEmpty())
            log("{0}", summary);
        else
            log("{0}: {1}", summary, detail);
    }

    final void outputError(String summary, Throwable exception) {
        addMessage(FacesMessage.SEVERITY_ERROR, summary,
                exception.getLocalizedMessage());
        log(summary, exception);
    }

    private void addMessage(Severity severity, String summary, String detail) {
        facesContext().addMessage(getClientId(),
                new FacesMessage(severity, summary, detail));
    }

    private void log(String format, Object... args) {
        externalContext().log(MessageFormat.format(format, args));
    }

    private void log(String message, Throwable exception) {
        externalContext().log(message, exception);
    }

    @Override
    public LicenseWizardState backState() {
        return LicenseWizardState.welcome;
    }

    @Override
    public LicenseWizardState nextState() {
        try {
            manager().verify();
            return LicenseWizardState.display;
        } catch (LicenseManagementException ex) {
            return LicenseWizardState.install;
        }
    }

    final boolean licenseInstalled() {
        try {
            manager().load();
            return true;
        } catch (LicenseManagementException ex) {
            return false;
        }
    }

    @Override
    public final void onBeforeStateSwitch() { }

    @Override
    public final void onAfterStateSwitch() { }
}

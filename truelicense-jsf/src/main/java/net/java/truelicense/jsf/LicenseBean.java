/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.jsf;

import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Nullable;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.*;
import javax.faces.context.*;
import net.java.truelicense.core.*;
import net.java.truelicense.core.util.Formattable;
import net.java.truelicense.ui.LicenseWizardState;
import net.java.truelicense.ui.util.MnemonicText;
import net.java.truelicense.ui.wizard.WizardView;

/**
 * A base class for backing beans for license consumer management.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public abstract class LicenseBean
extends UINamingContainer
implements WizardView<LicenseWizardState> {

    private @Nullable LicenseConsumerManager manager;

    LicenseBean() { }

    final LicenseConsumerManager manager() {
        final LicenseConsumerManager manager = this.manager;
        return null != manager ? manager : (this.manager = resolveManager());
    }

    private LicenseConsumerManager resolveManager() {
        final Object manager = getAttributes().get("manager");
        if (!(manager instanceof LicenseConsumerManager))
            throw new IllegalStateException("Missing license consumer manager in attributes map.");
        return (LicenseConsumerManager) manager;
    }

    final String message(Formattable key) {
        return message(key, subject());
    }

    final String message(Formattable key, Object... args) {
        return new MnemonicText(key.format(args).toString(locale())).getText();
    }

    final String subject() { return manager().subject(); }

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

    @Override public final void onBeforeStateSwitch() { }
    @Override public final void onAfterStateSwitch() { }
}

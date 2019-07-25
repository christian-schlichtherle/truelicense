/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.jsf;

import global.namespace.fun.io.api.Source;
import global.namespace.truelicense.api.LicenseManagementException;

import javax.faces.component.FacesComponent;
import javax.servlet.http.Part;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static global.namespace.truelicense.ui.LicenseWizardMessage.*;

/**
 * The backing bean for installing a license key.
 */
@FacesComponent
public final class InstallBean extends LicenseBean {

    public String getTitle() { return message(install_title); }
    public String getPrompt() { return message(install_prompt); }
    public String getSuccess() { return message(install_success); }
    public String getFailure() { return message(install_failure); }

    public String getInstallValue() { return message(install_install); }
    public boolean isInstallDisabled() { return false; } // TODO

    public String installAction() {
        try {
            manager().install(source());
            outputInfo(getSuccess());
        } catch (LicenseManagementException ex) {
            outputError(getFailure(), ex);
        }
        return null;
    }

    private Source source() { return () -> () -> getPart().getInputStream(); }

    public Part getPart() { return (Part) getStateHelper().get("part"); }

    public void setPart(@NotNull Part part) {
        getStateHelper().put("part", Objects.requireNonNull(part));
    }
}

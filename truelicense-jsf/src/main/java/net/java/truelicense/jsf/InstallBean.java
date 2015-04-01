/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.jsf;

import java.io.*;
import javax.faces.component.FacesComponent;
import javax.servlet.http.Part;
import javax.validation.constraints.NotNull;
import net.java.truelicense.core.LicenseManagementException;
import net.java.truelicense.core.io.Source;
import net.java.truelicense.core.util.Objects;
import static net.java.truelicense.ui.LicenseWizardMessage.*;

/**
 * The backing bean for installing a license key.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@FacesComponent("")
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

    private Source source() {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return getPart().getInputStream();
            }
        };
    }

    public Part getPart() { return (Part) getStateHelper().get("part"); }

    public void setPart(@NotNull Part part) {
        getStateHelper().put("part", Objects.requireNonNull(part));
    }
}

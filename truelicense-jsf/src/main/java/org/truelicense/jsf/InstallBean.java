/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.jsf;

import java.io.*;
import javax.faces.component.FacesComponent;
import javax.servlet.http.Part;
import javax.validation.constraints.NotNull;
import org.truelicense.api.LicenseManagementException;
import org.truelicense.api.io.Source;
import java.util.Objects;
import static org.truelicense.ui.LicenseWizardMessage.*;

/**
 * The backing bean for installing a license key.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
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

/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.jsf;

import global.namespace.truelicense.api.License;
import global.namespace.truelicense.api.LicenseManagementException;

import javax.faces.component.FacesComponent;
import javax.security.auth.x500.X500Principal;
import java.util.Date;

import static global.namespace.truelicense.ui.LicenseWizardMessage.*;

/**
 * The backing bean for displaying the installed license key.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@FacesComponent
public final class DisplayBean extends LicenseBean {

    private License license;

    public String getTitle() { return message(display_title); }

    public String getSubjectLabel() { return message(display_subject); }

    public String getSubjectValue() { return license().getSubject(); }

    public String getHolderLabel() { return message(display_holder); }

    public X500Principal getHolderValue() { return license().getHolder(); }

    public String getIssuerLabel() { return message(display_issuer); }

    public X500Principal getIssuerValue() { return license().getIssuer(); }

    public String getIssuedLabel() { return message(display_issued); }

    public Date getIssuedValue() { return license().getIssued(); }

    public String getNotBeforeLabel() { return message(display_notBefore); }

    public Date getNotBeforeValue() { return license().getNotBefore(); }

    public String getNotAfterLabel() { return message(display_notAfter); }

    public Date getNotAfterValue() { return license().getNotAfter(); }

    public String getConsumerLabel() { return message(display_consumer); }

    private String consumerType() { return license().getConsumerType(); }

    private int consumerAmount() { return license().getConsumerAmount(); }

    public String getConsumerValue() {
        return message(display_consumerFormat, subject(), consumerType(), consumerAmount());
    }

    public String getInfoLabel() { return message(display_info); }

    public String getInfoValue() { return license().getInfo(); }

    private License license() {
        final License l = license;
        return null != l ? l : (license = newLicense());
    }

    private License newLicense() {
        try {
            return manager().load();
        } catch (LicenseManagementException e) {
            return context().license();
        }
    }
}

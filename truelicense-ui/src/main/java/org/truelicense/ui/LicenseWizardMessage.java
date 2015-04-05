/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.ui;

import org.truelicense.core.util.Formattable;
import org.truelicense.core.util.Message;

import javax.annotation.CheckForNull;
import java.util.Date;

/**
 * Enumerates the messages of a license management wizard for license consumer
 * applications.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public enum LicenseWizardMessage implements Formattable {

    wizard_title,

    welcome_title, welcome_prompt, welcome_install, welcome_uninstall,
    welcome_display,

    install_title, install_prompt, install_select, install_install,
    install_success, install_failure,

    install_fileExtension, install_fileFilter,

    display_title, display_subject, display_holder, display_issuer,
    display_issued, display_notBefore, display_notAfter, display_consumer,
    display_info, display_failure,

    display_consumerFormat, display_dateTimeFormat,

    uninstall_title, uninstall_prompt, uninstall_uninstall, uninstall_success,
    uninstall_failure,

    failure_title;

    /**
     * @param args the formatting arguments.
     *             The first argument needs to be the licensing subject as
     *             returned by
     *             {@link org.truelicense.core.LicenseConsumerManager#subject()}.
     */
    @Override public Message format(Object... args) {
        return Messages.message(name(), args);
    }

    public static String display_dateTimeFormat(String subject, @CheckForNull Date date) {
        return null == date ? ""
                : display_dateTimeFormat.format(subject, date).toString();
    }
}

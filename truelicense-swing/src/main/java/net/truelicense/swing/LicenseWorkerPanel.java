/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.swing;

import net.truelicense.api.LicenseManagementException;
import net.truelicense.api.i18n.Message;
import net.truelicense.spi.i18n.BasicMessage;
import net.truelicense.ui.LicenseWizardMessage;

import javax.swing.*;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * An abstract {@code JPanel} for license consumer management.
 *
 * @author Christian Schlichtherle
 */
abstract class LicenseWorkerPanel extends LicensePanel {

    static final Message EMPTY_MESSAGE = new BasicMessage() {
        static final long serialVersionUID = 0L;

        @Override public String toString(Locale locale) { return ""; }
    };

    LicenseWorkerPanel(LicenseManagementWizard wizard) { super(wizard); }

    abstract void setStatusMessage(Message message);

    abstract Message successMessage();
    abstract Message failureMessage(Throwable throwable);

    static boolean isConsideredConfidential(Throwable ex) {
        return ex instanceof LicenseManagementException &&
                ((LicenseManagementException) ex).isConfidential();
    }

    @SuppressWarnings("PackageVisibleInnerClass")
    abstract class LicenseWorker extends SwingWorker<Void, Void> {

        @Override protected final void done() {
            try {
                get();
                setStatusMessage(successMessage());
            } catch (ExecutionException ex) {
                showExceptionDialog(ex.getCause());
            } catch (InterruptedException ex) {
                showExceptionDialog(ex);
            }

        }

        void showExceptionDialog(final Throwable ex) {
            assert isEventDispatchThread();
            JOptionPane.showMessageDialog(
                    LicenseWorkerPanel.this,
                    isConsideredConfidential(ex)
                        ? failureMessage(ex).toString()
                        : ex.getLocalizedMessage(),
                    LicenseWizardMessage.failure_title.format().toString(),
                    JOptionPane.ERROR_MESSAGE);

        }
    }
}

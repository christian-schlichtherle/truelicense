/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import static javax.swing.SwingUtilities.isEventDispatchThread;
import javax.swing.SwingWorker;
import org.truelicense.core.LicenseManagementException;
import org.truelicense.core.util.BaseMessage;
import org.truelicense.core.util.Message;
import org.truelicense.ui.LicenseWizardMessage;

/**
 * An abstract {@code JPanel} for license consumer management.
 *
 * @author Christian Schlichtherle
 */
abstract class LicenseWorkerPanel extends LicensePanel {

    static final Message EMPTY_MESSAGE = new BaseMessage() {
        static final long serialVersionUID = 0L;

        @Override public String toString(Locale locale) { return ""; }
    };

    LicenseWorkerPanel(LicenseWizard wizard) { super(wizard); }

    abstract void setStatusMessage(Message message);

    abstract Message successMessage();
    abstract Message failureMessage(Throwable throwable);

    static boolean isConsideredConfidential(Throwable ex) {
        return ex instanceof LicenseManagementException &&
                ((LicenseManagementException) ex).isConsideredConfidential();
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

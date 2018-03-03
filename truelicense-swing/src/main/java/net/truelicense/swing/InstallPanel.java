/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.swing;

import net.truelicense.api.ConsumerLicenseManager;
import net.truelicense.api.i18n.Message;
import net.truelicense.swing.util.ComponentEnabler;
import net.truelicense.swing.util.Enabler;
import net.truelicense.swing.util.EnhancedButton;
import net.truelicense.ui.LicenseWizardState;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;

import static net.truelicense.ui.LicenseWizardMessage.*;

/**
 * @author Christian Schlichtherle
 */
final class InstallPanel extends LicenseWorkerPanel {

    private static final long serialVersionUID = 1L;

    private final ConsumerLicenseManager manager;

    InstallPanel(final LicenseManagementWizard wizard) {
        super(wizard);
        final Enabler installButtonProxy = new ComponentEnabler() {
            static final long serialVersionUID = 0L;

            @Override protected JButton component() { return installButton; }
        };
        this.manager = new EnablingConsumerLicenseManager(wizard.nextButtonProxy(),
                new DisablingConsumerLicenseManager(installButtonProxy,
                        wizard.manager()));
        initComponents();
        final DocumentListener listener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                onFileFieldChange();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                onFileFieldChange();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                onFileFieldChange();
            }
        };
        fileField.getDocument().addDocumentListener(listener);
    }

    @Override
    ConsumerLicenseManager manager() { return manager; }

    private void onFileFieldChange() {
        final boolean valid = fileFieldContainsValidFilePath();
        installButton.setEnabled(valid);
        if (valid) installButton.requestFocusInWindow();
    }

    private boolean fileFieldContainsValidFilePath() {
        final String path = fileField.getText();
        return null != path && new File(path).isFile();
    }

    private FileFilter fileFilter() {
        class Filter extends FileFilter {
            final String description = install_fileFilter.format(
                    subject(), installFileExtension()).toString();

            @Override public String getDescription() {
                return description;
            }

            @Override public boolean accept(File file) {
                return file.isDirectory() ||
                        transform(file.getPath()).endsWith(installFileExtension());
            }

            String transform(String path) { return path; }
        }

        if (File.separatorChar == '\\') {
            return new Filter() {
                @Override public String transform(String path) {
                    return path.toLowerCase(Locale.ROOT);
                }
            };
        } else {
            return new Filter();
        }
    }

    private String installFileExtension() {
        final String extension = format(install_fileExtension);
        return extension.startsWith(".") ? extension : "." + extension;
    }

    @Override public LicenseWizardState nextState() {
        return LicenseWizardState.display;
    }

    @Override public void onAfterStateSwitch() {
        assert SwingUtilities.isEventDispatchThread();
        assert isVisible();
        wizard().nextButtonProxy().enabled(licenseInstalled());
        setStatusMessage(EMPTY_MESSAGE);
        fileButton.requestFocusInWindow();
        onFileFieldChange();
    }

    @Override void setStatusMessage(Message message) {
        status.setText(message.toString());
    }

    @Override Message successMessage() {
        return install_success.format(subject());
    }

    @Override Message failureMessage(Throwable throwable) {
        return install_failure.format(subject());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filechooser = new javax.swing.JFileChooser();
        javax.swing.JTextArea prompt = new javax.swing.JTextArea();
        fileButton = new EnhancedButton();
        installButton = new EnhancedButton();
        status = new javax.swing.JTextArea();

        filechooser.setFileFilter(fileFilter());

        setName(InstallPanel.class.getSimpleName());
        setLayout(new java.awt.GridBagLayout());

        prompt.setEditable(false);
        prompt.setFont(getFont());
        prompt.setLineWrap(true);
        prompt.setText(format(install_prompt)); // NOI18N
        prompt.setWrapStyleWord(true);
        prompt.setBorder(null);
        prompt.setName(install_prompt.name());
        prompt.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 0);
        add(prompt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        add(fileField, gridBagConstraints);

        fileButton.setText(format(install_select)); // NOI18N
        fileButton.setName(install_select.name());
        fileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(fileButton, gridBagConstraints);

        installButton.setText(format(install_install)); // NOI18N
        installButton.setName(install_install.name());
        installButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        add(installButton, gridBagConstraints);

        status.setEditable(false);
        status.setFont(getFont());
        status.setBorder(null);
        status.setName("status"); // NOI18N
        status.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        add(status, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void installActionPerformed(java.awt.event.ActionEvent ignored) {//GEN-FIRST:event_installActionPerformed
        new LicenseWorker() {
            @Override protected Void doInBackground() throws Exception {
                manager().install(manager().context().pathStore(Paths.get(fileField.getText())).input());
                return null;
            }

            @Override void showExceptionDialog(final Throwable ex) {
                super.showExceptionDialog(ex);
                fileButton.requestFocusInWindow();
            }
        }.execute();
    }//GEN-LAST:event_installActionPerformed

    private void fileButtonActionPerformed(java.awt.event.ActionEvent ignored) {//GEN-FIRST:event_fileButtonActionPerformed
        if (filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            fileField.setText(filechooser.getSelectedFile().getPath());
    }//GEN-LAST:event_fileButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private EnhancedButton fileButton;
    private final javax.swing.JTextField fileField = new javax.swing.JTextField();
    private javax.swing.JFileChooser filechooser;
    private EnhancedButton installButton;
    private javax.swing.JTextArea status;
    // End of variables declaration//GEN-END:variables
}

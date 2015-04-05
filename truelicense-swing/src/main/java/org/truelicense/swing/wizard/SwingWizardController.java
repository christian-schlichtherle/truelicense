/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing.wizard;

import java.awt.*;
import java.awt.event.*;
import javax.annotation.CheckForNull;
import javax.swing.*;
import javax.swing.border.*;
import org.truelicense.core.util.Objects;
import org.truelicense.swing.util.*;
import org.truelicense.ui.wizard.*;
import static org.truelicense.ui.wizard.WizardMessage.*;

/**
 * A generic wizard which uses a {@link JDialog}.
 *
 * @param <S> the type of the wizard's states.
 * @param <V> the type of the wizard's views.
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public abstract class SwingWizardController<
        S extends Enum<S>,
        V extends Component & WizardView<S>>
extends BasicWizardController<S, V> {

    /** Indicates which button was pressed to close the wizard dialog. */
    // Don't change order: The ordinal may be used by clients!
    public enum ReturnCode { finish, cancel }

    private final JDialog dialog;
    private final JButton cancelButton, backButton, nextButton;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private ReturnCode lastReturnCode = ReturnCode.cancel;

    /**
     * Constructs a wizard controller for the given owner dialog.
     *
     * @param owner the owner dialog.
     */
    protected SwingWizardController(@CheckForNull Dialog owner) {
        this(new EnhancedDialog(owner));
    }

    /**
     * Constructs a wizard controller for the given owner frame.
     *
     * @param owner the owner frame.
     */
    protected SwingWizardController(@CheckForNull Frame owner) {
        this(new EnhancedDialog(owner));
    }

    /**
     * Constructs a wizard controller which uses the given dialog.
     *
     * @param dialog the dialog to use.
     */
    protected SwingWizardController(final EnhancedDialog dialog) {
        final ActionListener listener = new ActionListener() {
            @Override public void actionPerformed(final ActionEvent e) {
                final WizardMessage key = WizardMessage.valueOf(e.getActionCommand());
                if      (wizard_cancel.equals(key)) cancel();
                else if (wizard_back.equals(key))   switchBack();
                else if (wizard_next.equals(key))   switchNext();
                else if (wizard_finish.equals(key)) finish();
                else                                throw new AssertionError();
            }
        };

        cancelButton = new EnhancedButton();
        cancelButton.setName(wizard_cancel.name());
        cancelButton.setActionCommand(wizard_cancel.name());
        cancelButton.addActionListener(listener);

        backButton = new EnhancedButton();
        backButton.setName(wizard_back.name());
        backButton.setActionCommand(wizard_back.name());
        backButton.addActionListener(listener);

        nextButton = new EnhancedButton();
        nextButton.setName(wizard_next.name());
        nextButton.setActionCommand(wizard_next.name());
        nextButton.addActionListener(listener);

        final Border border = new EmptyBorder(new Insets(10, 10, 10, 10));

        final Box buttonBox = new Box(BoxLayout.LINE_AXIS);
        buttonBox.setBorder(border);
        buttonBox.add(backButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(nextButton);
        buttonBox.add(Box.createHorizontalStrut(30));
        buttonBox.add(cancelButton);

        //  Create the buttons with a separator above them, then place them
        //  on the east side of the view with a small amount of space between
        //  the back and the next button, and a larger amount of space between
        //  the next button and the cancel button.
        final JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(new JSeparator(), BorderLayout.NORTH);
        controlPanel.add(buttonBox, BorderLayout.EAST);

        cardLayout = new CardLayout();

        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(border);

        this.dialog = Objects.requireNonNull(dialog);
        dialog.setName(getClass().getSimpleName());
        dialog.setLayout(new BorderLayout());
        dialog.add(cardPanel, BorderLayout.CENTER);
        dialog.add(controlPanel, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(nextButton);
    }

    @Override protected void view(S state, V view) {
        super.view(state, view);
        cardPanel.add(view, state.name());
    }

    /** Returns an enabler for the next-button. */
    protected ComponentEnabler nextButtonProxy() {
        return new ComponentEnabler() {
            static final long serialVersionUID = 0L;

            @Override protected JButton component() { return nextButton; }

            @Override public void enabled(final boolean value) {
                assert SwingUtilities.isEventDispatchThread();
                super.enabled(value);
                requestFocusOnBackButtonIfEnabled();
                requestFocusOnNextButtonIfEnabled();
            }
        };
    }

    /** Packs and displays the modal wizard dialog. */
    public ReturnCode showModalDialog() {
        fireAfterStateSwitch();
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.setModal(true);
        dialog.setVisible(true);
        return lastReturnCode;
    }

    /** Returns the last return code from the wizard dialog. */
    public ReturnCode lastReturnCode() { return lastReturnCode; }

    @Override protected void onAfterStateSwitch() {
        cancelButton.setText(format(wizard_cancel));

        backButton.setText(format(wizard_back));
        backButton.setEnabled(switchBackEnabled());
        requestFocusOnBackButtonIfEnabled();

        final WizardMessage nextMessage =
                switchNextEnabled() ? wizard_next : wizard_finish;
        nextButton.setActionCommand(nextMessage.name());
        nextButton.setText(format(nextMessage));
        nextButton.setEnabled(true);
        requestFocusOnNextButtonIfEnabled();

        cardLayout.show(cardPanel, currentState().name());
    }

    private String format(WizardMessage key) { return key.format().toString(); }

    private void requestFocusOnBackButtonIfEnabled() {
        if (backButton.isEnabled()) backButton.requestFocusInWindow();
    }

    private void requestFocusOnNextButtonIfEnabled() {
        if (nextButton.isEnabled()) nextButton.requestFocusInWindow();
    }

    /**
     * Closes the modal wizard dialog and sets the return code to
     * {@link ReturnCode#cancel}.
     */
    protected void cancel() { close(ReturnCode.cancel); }

    /**
     * Closes the modal wizard dialog and sets the return code to
     * {@link ReturnCode#finish}.
     */
    protected void finish() { close(ReturnCode.finish); }

    private void close(final ReturnCode rc) {
        dialog.setVisible(false);
        lastReturnCode = rc;
    }
}

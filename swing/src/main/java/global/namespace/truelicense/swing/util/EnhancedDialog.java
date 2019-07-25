/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.swing.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.Serializable;

/**
 * A dialog which performs the default closing operation when the escape key
 * gets pressed.
 */
public class EnhancedDialog extends JDialog {

    private static final long serialVersionUID = 0L;

    public EnhancedDialog() throws HeadlessException {
        super();
    }

    public EnhancedDialog(Frame nullableOwner) throws HeadlessException {
        super(nullableOwner);
    }

    public EnhancedDialog(Frame nullableOwner, boolean modal) throws HeadlessException {
        super(nullableOwner, modal);
    }

    public EnhancedDialog(Frame nullableOwner, String title) throws HeadlessException {
        super(nullableOwner, title);
    }

    public EnhancedDialog(Frame nullableOwner, String title, boolean modal) throws HeadlessException {
        super(nullableOwner, title, modal);
    }

    public EnhancedDialog(Frame nullableOwner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
        super(nullableOwner, title, modal, gc);
    }

    public EnhancedDialog(Dialog nullableOwner) throws HeadlessException {
        super(nullableOwner);
    }

    public EnhancedDialog(Dialog nullableOwner, boolean modal) throws HeadlessException {
        super(nullableOwner, modal);
    }

    public EnhancedDialog(Dialog nullableOwner, String title) throws HeadlessException {
        super(nullableOwner, title);
    }

    public EnhancedDialog(Dialog nullableOwner, String title, boolean modal) throws HeadlessException {
        super(nullableOwner, title, modal);
    }

    public EnhancedDialog(Dialog nullableOwner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
        super(nullableOwner, title, modal, gc);
    }

    @Override protected JRootPane createRootPane() {
        final JRootPane rootPane = super.createRootPane();
        class Listener implements ActionListener, Serializable {
            static final long serialVersionUID = 0L;

            @Override public void actionPerformed(ActionEvent evt) {
                onEscapeKeyPressed();
            }
        };
        final Listener listener = new Listener();
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        final int condition = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
        rootPane.registerKeyboardAction(listener, keyStroke, condition);
        return rootPane;
    }

    /**
     * Called when the escape key has been pressed.
     * The implementation in the {@code EnhancedDialog} performs the default
     * close operation.
     *
     * @see #setDefaultCloseOperation(int)
     */
    protected void onEscapeKeyPressed() {
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /** Forwards the call to the root pane. */
    public final void setDefaultButton(JButton button) {
        getRootPane().setDefaultButton(button);
    }

    /** Forwards the call to the root pane. */
    public final JButton getDefaultButton() {
        return getRootPane().getDefaultButton();
    }
}

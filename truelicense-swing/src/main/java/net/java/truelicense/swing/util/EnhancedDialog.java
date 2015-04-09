/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.swing.util;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import javax.annotation.CheckForNull;
import javax.swing.*;

/**
 * A dialog which performs the default closing operation when the escape key
 * gets pressed.
 *
 * @author Christian Schlichtherle
 */
public class EnhancedDialog extends JDialog {

    private static final long serialVersionUID = 0L;

    public EnhancedDialog() throws HeadlessException {
        super();
    }

    public EnhancedDialog(@CheckForNull Frame owner)
    throws HeadlessException {
        super(owner);
    }

    public EnhancedDialog(@CheckForNull Frame owner, boolean modal)
    throws HeadlessException {
        super(owner, modal);
    }

    public EnhancedDialog(@CheckForNull Frame owner, String title)
    throws HeadlessException {
        super(owner, title);
    }

    public EnhancedDialog(
            @CheckForNull Frame owner,
            String title,
            boolean modal)
    throws HeadlessException {
        super(owner, title, modal);
    }

    public EnhancedDialog(
            @CheckForNull Frame owner,
            String title,
            boolean modal,
            GraphicsConfiguration gc)
    throws HeadlessException {
        super(owner, title, modal, gc);
    }

    public EnhancedDialog(@CheckForNull Dialog owner)
            throws HeadlessException {
        super(owner);
    }

    public EnhancedDialog(@CheckForNull Dialog owner, boolean modal)
            throws HeadlessException {
        super(owner, modal);
    }

    public EnhancedDialog(@CheckForNull Dialog owner, String title)
            throws HeadlessException {
        super(owner, title);
    }

    public EnhancedDialog(
            @CheckForNull Dialog owner,
            String title,
            boolean modal)
            throws HeadlessException {
        super(owner, title, modal);
    }

    public EnhancedDialog(
            @CheckForNull Dialog owner,
            String title,
            boolean modal,
            GraphicsConfiguration gc)
            throws HeadlessException {
        super(owner, title, modal, gc);
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

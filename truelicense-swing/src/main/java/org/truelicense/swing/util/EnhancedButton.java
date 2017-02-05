/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.swing.util;

import org.truelicense.ui.misc.MnemonicText;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * The {@link #setText(String)} method scans the string for the first
 * occurrence of the character {@code &} to set the button's mnemonic.
 *
 * @author Christian Schlichtherle
 */
public class EnhancedButton extends JButton {

    private static final long serialVersionUID = 0L;

    public EnhancedButton() {
        super();
    }

    public EnhancedButton(Icon icon) {
        super(icon);
    }

    public EnhancedButton(String text) {
        super(text);
    }

    public EnhancedButton(Action a) {
        super(a);
    }

    public EnhancedButton(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * Sets the text of the button whereby the first single occurence of the
     * character {@code '&'} is used to determine the next character as the
     * mnemonic for this button.
     * <p>
     * All single occurences of {@code '&'} are removed from the text
     * and all double occurences are replaced by a single {@code '&'}
     * before passing the result to the super classes implementation.
     * <p>
     * Note that if the resulting text is HTML, the index of the mnemonic
     * character is ignored and the look and feel will (if at all) highlight
     * the first occurence of the mnemonic character.
     */
    @Override public void setText(final String text) {
        if (null != text) {
            final MnemonicText mt = new MnemonicText(text);
            super.setText(mt.toString());
            if (0 <= mt.getMnemonicIndex()) {
                setMnemonic(mt.getMnemonic());
                if (!mt.isHtmlText())
                    setDisplayedMnemonicIndex(mt.getMnemonicIndex());
            }
        } else {
            super.setText(null);
        }
    }
}

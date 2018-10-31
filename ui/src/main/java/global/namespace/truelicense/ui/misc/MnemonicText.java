/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.ui.misc;

import java.io.Serializable;

/**
 * Decodes the constructor's string parameter into a text and a mnemonic
 * character.
 *
 * retrieve extended information
 * which can be used to configure the mnemonic for a {@code JComponent}.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public final class MnemonicText implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String HTML_TAG = "<html>";

    private final String text;
    private final int mnemonicIndex;
    private final char mnemonic;
    private final boolean isHtmlText;

    /**
     * Scans the given {@code text} for the character {@code '&'} to
     * retrieve the mnemonic and its index.
     * <p>
     * Every occurence of {@code '&'} will be removed from the text unless
     * it is followed by another {@code '&'}
     * in which case this sequence is substituted by a single {@code '&'}.
     */
    public MnemonicText(final String text) {
        int mnemonicIndex = -1;
        char mnemonic = 0;
        final StringBuilder buf = new StringBuilder(text.length());
        int l = text.length();
        for (int i = 0; i < l; i++) {
            char c = text.charAt(i);
            if (c == '&') {
                if (++i >= l) // skip this ampersand
                    break;
                if ((c = text.charAt(i)) != '&' && mnemonicIndex == -1)  {
                    mnemonic = c;
                    mnemonicIndex = buf.length();
                }
            }
            buf.append(c);
        }
        if (buf.length() == l) this.text = text; // text and buf are equal
        else this.text = buf.toString();

        this.mnemonicIndex = mnemonicIndex;
        this.mnemonic = mnemonic;
        final String trim = text.trim();
        isHtmlText = trim.substring(0, Math.min(HTML_TAG.length(), trim.length()))
                .equalsIgnoreCase(HTML_TAG);
    }

    /**
     * Returns the decoded text.
     *
     * @return the decoded text.
     */
    @Override public String toString() { return getText(); }

    /** Returns the decoded text. */
    public String getText() { return text; }

    /**
     * Returns {@code true} if and only if the decoded text starts with an
     * {@code <html>} tag, whereby case is ignored.
     */
    public boolean isHtmlText() { return isHtmlText; }

    /** Returns the index of the mnemonic character or -1 if not found. */
    public int getMnemonicIndex() { return mnemonicIndex; }

    /**
     * Returns the mnemonic character.
     *
     * @throws IllegalStateException if there is no mnemonic character.
     */
    public char getMnemonic() {
        if (0 > mnemonicIndex) throw new IllegalStateException();
        return mnemonic;
    }
}

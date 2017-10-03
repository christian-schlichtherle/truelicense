/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.ui.wizard;

/**
 * A controller for a generic wizard user interface.
 *
 * @author Christian Schlichtherle
 * @since  TrueLicense 2.3
 */
public interface WizardController {

    /**
     * Returns {@code true} if and only if the back-state of the current view
     * does not equal the current state of the model.
     */
    boolean switchBackEnabled();

    /**
     * Switches to the back-state of the current view.
     * If the back-state of the current view equals the current state of the
     * model, then nothing happens.
     * Otherwise, the current view gets hidden and the view for the back-state
     * gets shown.
     */
    void switchBack();

    /**
     * Returns {@code true} if and only if the next-state of the current view
     * does not equal the current state of the model.
     */
    boolean switchNextEnabled();

    /**
     * Switches to the next-state of the current view.
     * If the next-state of the current view equals the current state of the
     * model, then nothing happens.
     * Otherwise, the current view gets hidden and the view for the next-state
     * gets shown.
     */
    void switchNext();
}

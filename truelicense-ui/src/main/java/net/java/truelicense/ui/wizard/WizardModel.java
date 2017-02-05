/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.ui.wizard;

/**
 * A model for a generic wizard user interface.
 *
 * @param  <S> the type of the wizard's states.
 * @param  <V> the type of the wizard's views.
 * @author Christian Schlichtherle
 * @since  TrueLicense 2.3
 */
public interface WizardModel<S, V> {

    /** Returns the current state. */
    S currentState();

    /** Sets the current state. */
    void currentState(S state);

    /** Returns the view for the given state. */
    V view(S state);

    /** Sets the view for the given state. */
    void view(S state, V view);
}

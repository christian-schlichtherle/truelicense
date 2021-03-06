/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.ui.wizard;

/**
 * A model for a generic wizard user interface.
 *
 * @param  <S> the type of the wizard's states.
 * @param  <V> the type of the wizard's views.
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

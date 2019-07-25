/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.ui.wizard;

/**
 * A view for a generic wizard user interface.
 * <ul>
 * <li>In a Swing app, this could be a {@link javax.swing.JPanel}.
 * <li>In a JSF app, this could be the backing bean in a Facelet.
 * </ul>
 *
 * @param  <S> the type of the wizard's states.
 */
public interface WizardView<S> {

    /**
     * Returns the state to switch to when the back-button gets pressed,
     * or the current state if switching is not possible.
     */
    S backState();

    /**
     * Returns the state to switch to when the next-button gets pressed,
     * or the current state if switching is not possible.
     */
    S nextState();

    /**
     * This hook gets called by the controller before the state of the model
     * changes.
     * A typical implementation may want to hide this view.
     */
    void onBeforeStateSwitch();

    /**
     * This hook gets called by the controller after the state of the model
     * has changed.
     * A typical implementation may want to show this view.
     */
    void onAfterStateSwitch();
}

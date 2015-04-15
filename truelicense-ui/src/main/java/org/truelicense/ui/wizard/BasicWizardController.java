/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.ui.wizard;

/**
 * A basic implementation of a wizard controller.
 * This class is immutable.
 *
 * @param  <S> the type of the wizard's states.
 * @param  <V> the type of the wizard's views.
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public abstract class BasicWizardController<S, V extends WizardView<S>>
implements WizardController {

    /**
     * Creates a new wizard controller with the given model.
     *
     * @param model the wizard model.
     * @param <S> the type of the wizard's states.
     * @param <V> the type of the wizard's views.
     */
    public static <S, V extends WizardView<S>> WizardController create(
            final WizardModel<S, V> model) {
        return new BasicWizardController<S, V>() {
            @Override protected WizardModel<S, V> model() { return model; }
        };
    }

    /** Returns the wizard model. */
    protected abstract WizardModel<S, V> model();

    @Override public final boolean switchBackEnabled() {
        return !backState().equals(currentState());
    }

    @Override public final void switchBack() { switchTo(backState()); }

    @Override public final boolean switchNextEnabled() {
        return !nextState().equals(currentState());
    }

    @Override public final void switchNext() { switchTo(nextState()); }

    /** Switches from the current state to the given state. */
    private void switchTo(final S newState) {
        if (newState.equals(currentState())) return;
        fireBeforeStateSwitch();
        currentState(newState);
        fireAfterStateSwitch();
    }

    /**
     * Notifies the current view of the event by calling its
     * {@link WizardView#onBeforeStateSwitch} method and then calls this
     * controller's {@link #onBeforeStateSwitch} hook.
     * This method gets called before this controller switches the state of
     * the model.
     */
    protected final void fireBeforeStateSwitch() {
        currentView().onBeforeStateSwitch();
        onBeforeStateSwitch();
    }

    /**
     * Override this template method in order to add some behaviour before this
     * controller switches the state of the model, but after the current view
     * has been notified via {@link WizardView#onBeforeStateSwitch}.
     *
     * @see #currentView
     */
    protected void onBeforeStateSwitch() { }

    /**
     * Calls this controller's {@link #onAfterStateSwitch} hook and then
     * notifies the current view of the event by calling its
     * {@link WizardView#onAfterStateSwitch} method.
     * This method gets called after this controller has switched the state of
     * the model.
     */
    protected final void fireAfterStateSwitch() {
        onAfterStateSwitch();
        currentView().onAfterStateSwitch();
    }

    /**
     * Override this template method in order to add some behaviour after this
     * controller has switched the state of the model, but before the current
     * view gets notified via {@link WizardView#onAfterStateSwitch}.
     *
     * @see #currentView
     */
    protected void onAfterStateSwitch() { }

    /** Returns the view of the current state. */
    protected final V currentView() { return view(currentState()); }

    /** Returns the view for the given state. */
    protected final V view(S state) { return model().view(state); }

    /** Sets the view for the given state. */
    protected void view(S state, V view) { model().view(state, view); }

    /** Returns the current state. */
    protected final S currentState() { return model().currentState(); }

    /** Sets the current state. */
    private void currentState(S state) { model().currentState(state); }

    /**
     * Returns the state to switch to when the back-button gets pressed,
     * or the current state if switching is not possible.
     */
    protected final S backState() { return currentView().backState(); }

    /**
     * Returns the state to switch to when the next-button gets pressed,
     * or the current state if switching is not possible.
     */
    protected final S nextState() { return currentView().nextState(); }
}

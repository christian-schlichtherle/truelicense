/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.ui.wizard;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * A basic implementation of a wizard model.
 *
 * @param  <S> the type of the wizard's states.
 * @param  <V> the type of the wizard's views.
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public class BasicWizardModel<S, V> implements WizardModel<S, V> {

    /**
     * Creates a new wizard model for the given enum class.
     * The model will have no views and it's current state will be the first
     * enum object.
     *
     * @param clazz the clazz of the enum type which represents the wizard's
     *              state.
     * @param <S> the enum type of the wizard's states.
     * @param <V> the type of the wizard's views.
     */
    public static <S extends Enum<S>, V> WizardModel<S, V> create(
            final Class<S> clazz) {
        return new BasicWizardModel<>(new EnumMap<S, V>(clazz),
                clazz.getEnumConstants()[0]);
    }

    private final Map<S, V> views;
    private S state;

    /**
     * Constructs a new wizard model with the given map of views and state.
     *
     * @param views the map of views, which will be shared with the caller.
     * @param state the initial state of the constructed model;
     */
    protected BasicWizardModel(final Map<S, V> views, final S state) {
        this.views = Objects.requireNonNull(views);
        this.state = Objects.requireNonNull(state);
    }

    @Override public S currentState() { return state; }

    @Override public void currentState(final S state) {
        this.state = Objects.requireNonNull(state);
    }

    @Override public V view(S state) { return views.get(state); }

    @Override public void view(S state, V view) {
        views.put(state, Objects.requireNonNull(view));
    }
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.util;

/**
 * Injects a dependency into some target.
 *
 * @param  <Target> the type of the target.
 * @author Christian Schlichtherle
 */
public interface Injection<Target> {

    /** Injects a dependency into the target and returns the target. */
    Target inject();
}

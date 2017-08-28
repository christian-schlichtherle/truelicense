/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.misc;

import java.util.Date;

/**
 * A factory for representations of the current date/time.
 *
 * @author Christian Schlichtherle
 */
public interface Clock {

    /**
     * Returns a new representation of the current date/time.
     * A basic implementation could just return a new {@code Date}.
     * However, in order to protect against date/time forgery, a more
     * sophisticated implementation should use an authoritative time source,
     * e.g. a radio clock, an Internet time server or extrapolate an
     * approximation from timestamps in input data.
     * If resolving the current date/time from an authoritative resource fails
     * for any reason, the implementation should simply fallback to return a
     * new {@code Date}.
     */
    Date now();
}

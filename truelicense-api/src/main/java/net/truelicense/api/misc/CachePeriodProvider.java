/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.misc;

/**
 * Provides a cache period.
 *
 * @author Christian Schlichtherle
 */
public interface CachePeriodProvider {

    /**
     * Returns the cache period for external changes to the license key in milliseconds.
     * Any non-negative value is valid.
     * Return {@link Long#MAX_VALUE} to disable the timeout or zero to disable the caching of intermediate results.
     */
    long cachePeriodMillis();
}

/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core;

import java.util.Optional;

import static java.lang.System.currentTimeMillis;

/**
 * A simple cache with just one association.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class Cache<K, V> {

    private final Optional<K> optKey;
    private final Optional<V> optValue;
    private final long cachePeriodMillis;
    private final long startTimeMillis = currentTimeMillis();

    Cache() {
        this(Optional.empty(), Optional.empty(), 0); } // => obsolete() == true

    Cache(final Optional<K> optKey, final Optional<V> optValue, final long cachePeriodMillis) {
        this.optKey = optKey;
        this.optValue = optValue;
        if (0 > (this.cachePeriodMillis = cachePeriodMillis))
            throw new IllegalArgumentException();
    }

    Cache<K, V> key(Optional<K> optKey) {
        return hasKey(optKey) ? this : new Cache<>(optKey, optValue, cachePeriodMillis);
    }

    Optional<V> map(Optional<K> optKey) {
        return hasKey(optKey) && !obsolete() ? optValue : Optional.empty();
    }

    boolean hasKey(Optional<K> optKey) {
        return optKey.equals(this.optKey);
    }

    boolean obsolete() {
        return currentTimeMillis() - startTimeMillis >= cachePeriodMillis;
    }
}

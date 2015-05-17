/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.spi.misc.Option;

import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * A simple cache with just one association.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
final class Cache<K, V> {

    private final List<K> optKey;
    private final List<V> optValue;
    private final long cachePeriodMillis;
    private final long startTimeMillis = currentTimeMillis();

    Cache() { this(Option.<K>none(), Option.<V>none(), 0); } // => obsolete() == true

    Cache(final List<K> optKey, final List<V> optValue, final long cachePeriodMillis) {
        this.optKey = optKey;
        this.optValue = optValue;
        if (0 > (this.cachePeriodMillis = cachePeriodMillis))
            throw new IllegalArgumentException();
    }

    Cache<K, V> key(List<K> optKey) {
        return hasKey(optKey) ? this : new Cache<>(optKey, optValue, cachePeriodMillis);
    }

    List<V> map(List<K> optKey) {
        return hasKey(optKey) && !obsolete() ? optValue : Option.<V>none();
    }

    boolean hasKey(List<K> optKey) {
        return optKey.equals(this.optKey);
    }

    boolean obsolete() {
        return currentTimeMillis() - startTimeMillis >= cachePeriodMillis;
    }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import static java.lang.System.*;
import java.util.AbstractMap.SimpleEntry;
import javax.annotation.*;
import javax.annotation.concurrent.Immutable;

/**
 * A cache entry.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public class CacheEntry<K, V> extends SimpleEntry<K, V> {

    private static final long serialVersionUID = 1L;

    private final long cachePeriodMillis;
    private transient long startTimeMillis = currentTimeMillis();

    /**
     * Constructs a new cache entry.
     * The new cache entry will have {@link Long#MAX_VALUE} as its timeout.
     *
     * @param key the key.
     * @param value the value.
     */
    public CacheEntry(@CheckForNull K key, @CheckForNull V value) {
        this(key, value, Long.MAX_VALUE);
    }

    /**
     * Constructs a new cache entry.
     *
     * @param key the key.
     * @param value the value.
     * @param cachePeriodMillis the cache period of this entry in milliseconds.
     */
    public CacheEntry(
            final @CheckForNull K key,
            final @CheckForNull V value,
            final long cachePeriodMillis) {
        super(key, value);
        if (0 > (this.cachePeriodMillis = cachePeriodMillis))
            throw new IllegalArgumentException();
    }

    private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        startTimeMillis = System.currentTimeMillis();
    }

    /** Returns the time of cache entry creation. */
    public long getStartTimeMillis() { return startTimeMillis; }

    /** Returns the cache period in milliseconds. */
    public long getCachePeriodMillis() { return cachePeriodMillis;   }

    /**
     * Maps the given key to the value of this cache entry if it
     * {@linkplain #matches matches} or otherwise returns {@code null}.
     */
    public @CheckForNull V map(@CheckForNull K key) {
        return matches(key) ? getValue() : null;
    }

    /**
     * Returns {@code true} if and only if the given key
     * {@linkplain Object#equals equals} the key of this cache entry and this
     * cache entry is not {@linkplain #isObsolete obsolete}.
     *
     * @param key the key to test.
     */
    public boolean matches(@CheckForNull K key) {
        return Objects.equals(key, getKey()) && !isObsolete();
    }

    /** Returns {@code true} if and only if the cache period has elapsed. */
    public boolean isObsolete() {
        return currentTimeMillis() - getStartTimeMillis() >=
                getCachePeriodMillis();
    }
}

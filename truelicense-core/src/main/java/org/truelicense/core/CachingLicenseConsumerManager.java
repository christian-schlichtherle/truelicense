/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.License;
import org.truelicense.api.LicenseManagementException;
import org.truelicense.api.auth.Artifactory;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.misc.CachePeriodProvider;
import org.truelicense.spi.misc.Option;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * A basic license consumer manager which caches some computed objects to speed
 * up subsequent requests.
 *
 * @author Christian Schlichtherle
 */
@ThreadSafe
abstract class CachingLicenseConsumerManager
extends BasicLicenseManager implements CachePeriodProvider {

    // These volatile fields get initialized by applying a pure function which
    // takes the immutable value of the store() property as its single argument.
    // So some concurrent threads may safely interleave when initializing these
    // fields without creating a racing condition and thus it's not generally
    // required to synchronize access to them.
    private volatile Cache<Source, Artifactory> cachedArtifactory = new Cache<>();
    private volatile Cache<Source, License> cachedLicense = new Cache<>();

    @Override
    public void install(final Source source)
    throws LicenseManagementException {
        final Store store = store();
        synchronized (store) {
            super.install(source);

            final List<Source> optSource = Option.wrap(source);
            final List<Source> optStore = Option.<Source>wrap(store);

            // As a side effect of the license key installation, the cached
            // artifactory and license get associated to the source unless this
            // is a re-installation from an equal source or the cached objects
            // have already been obsoleted by a time-out, that is, if the cache
            // period is equal or close to zero.
            assert cachedArtifactory.hasKey(optSource) ||
                    cachedArtifactory.hasKey(optStore) ||
                    cachedArtifactory.obsolete();
            assert cachedLicense.hasKey(optSource) ||
                    cachedLicense.hasKey(optStore) ||
                    cachedLicense.obsolete();

            // Update the association of the cached artifactory and license to
            // the store.
            cachedArtifactory = cachedArtifactory.key(optStore);
            cachedLicense = cachedLicense.key(optStore);
        }
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        final Cache<Source, Artifactory> cachedArtifactory = new Cache<>();
        final Cache<Source, License> cachedLicense = new Cache<>();
        synchronized (store()) {
            super.uninstall();
            this.cachedArtifactory = cachedArtifactory;
            this.cachedLicense = cachedLicense;
        }
    }

    @Override
    void validate(final Source source) throws Exception {
        final List<Source> optSource = Option.wrap(source);
        List<License> optLicense = cachedLicense.map(optSource);
        if (optLicense.isEmpty())
            cachedLicense = new Cache<>(optSource,
                    optLicense = Option.wrap(decodeLicense(source)),
                    cachePeriodMillis());
        validation().validate(optLicense.get(0));
    }

    @Override
    Artifactory authenticate(final Source source) throws Exception {
        final List<Source> optSource = Option.wrap(source);
        List<Artifactory> optArtifactory = cachedArtifactory.map(optSource);
        if (optArtifactory.isEmpty())
            cachedArtifactory = new Cache<>(optSource,
                    optArtifactory = Option.wrap(super.authenticate(source)),
                    cachePeriodMillis());
        return optArtifactory.get(0);
    }
}

@Immutable
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

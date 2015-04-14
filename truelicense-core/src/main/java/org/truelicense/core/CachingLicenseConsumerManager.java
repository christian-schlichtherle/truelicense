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
    private volatile CachedArtifactory cachedArtifactory = new CachedArtifactory();
    private volatile CachedLicense cachedLicense = new CachedLicense();

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
            // period is close to zero.
            assert cachedArtifactory.matches(optSource) ||
                    cachedArtifactory.matches(optStore) ||
                    cachedArtifactory.obsolete();
            assert cachedLicense.matches(optSource) ||
                    cachedLicense.matches(optStore) ||
                    cachedLicense.obsolete();

            // Update the association of the cached artifactory and license to
            // the store.
            this.cachedArtifactory = cachedArtifactory.optSource(optStore);
            this.cachedLicense = cachedLicense.optSource(optStore);
        }
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        final CachedArtifactory cachedArtifactory = new CachedArtifactory();
        final CachedLicense cachedLicense = new CachedLicense();
        synchronized (store()) {
            super.uninstall();
            this.cachedArtifactory = cachedArtifactory;
            this.cachedLicense = cachedLicense;
        }
    }

    @Override
    License validate(final Source source) throws Exception {
        final List<Source> optSource = Option.wrap(source);
        List<License> optLicense = cachedLicense.map(optSource);
        if (optLicense.isEmpty())
            this.cachedLicense = new CachedLicense(optSource,
                    optLicense = Option.wrap(decodeLicense(source)),
                    cachePeriodMillis());
        final License bean = optLicense.get(0);
        validation().validate(bean);
        return bean;
    }

    @Override
    Artifactory authenticate(final Source source) throws Exception {
        final List<Source> optSource = Option.wrap(source);
        List<Artifactory> optArtifactory = cachedArtifactory.map(optSource);
        if (optArtifactory.isEmpty())
            this.cachedArtifactory = new CachedArtifactory(optSource,
                    optArtifactory = Option.wrap(super.authenticate(source)),
                    cachePeriodMillis());
        return optArtifactory.get(0);
    }
}

@Immutable
final class CachedArtifactory extends CacheEntry<Source, Artifactory> {

    CachedArtifactory() { this(Option.<Source>none(), Option.<Artifactory>none(), 0); } // => obsolete() == true

    CachedArtifactory(
            List<Source> optSource,
            List<Artifactory> optArtifactory,
            long timeoutPeriodMillis) {
        super(optSource, optArtifactory, timeoutPeriodMillis);
    }

    CachedArtifactory optSource(List<Source> optSource) {
        return new CachedArtifactory(optSource, optValue, cachePeriodMillis);
    }
}

@Immutable
final class CachedLicense extends CacheEntry<Source, License> {

    CachedLicense() { this(Option.<Source>none(), Option.<License>none(), 0); } // => obsolete() == true

    CachedLicense(
            List<Source> optSource,
            List<License> optLicense,
            long timeoutPeriodMillis) {
        super(optSource, optLicense, timeoutPeriodMillis);
    }

    CachedLicense optSource(List<Source> optSource) {
        return new CachedLicense(optSource, optValue, cachePeriodMillis);
    }
}

@Immutable
class CacheEntry<K, V> {

    final List<K> optKey;
    final List<V> optValue;
    final long cachePeriodMillis;
    final long startTimeMillis = currentTimeMillis();

    CacheEntry(final List<K> optKey, final List<V> optValue, final long cachePeriodMillis) {
        this.optKey = optKey;
        this.optValue = optValue;
        if (0 > (this.cachePeriodMillis = cachePeriodMillis))
            throw new IllegalArgumentException();
    }

    List<V> map(List<K> optKey) {
        return matches(optKey) ? optValue : Option.<V>none();
    }

    boolean matches(List<K> optKey) {
        return optKey.equals(this.optKey) && !obsolete();
    }

    boolean obsolete() {
        return currentTimeMillis() - startTimeMillis >= cachePeriodMillis;
    }
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import javax.annotation.CheckForNull;
import javax.annotation.concurrent.*;
import org.truelicense.core.auth.Artifactory;
import org.truelicense.core.io.*;
import org.truelicense.core.util.*;

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
    private volatile CachedArtifactory
            cachedArtifactory = new CachedArtifactory();
    private volatile CachedLicense
            cachedLicense = new CachedLicense();

    @Override
    public void install(final Source source)
    throws LicenseManagementException {
        final Store store = store();
        synchronized (store) {
            super.install(source);

            // As a side effect of the license key installation, the cached
            // artifactory and license get associated to the source unless this
            // is a re-installation from an equal source or the cached objects
            // have already been obsoleted by a time-out, that is, if the cache
            // period is close to zero.
            assert cachedArtifactory.matches(source) ||
                    cachedArtifactory.matches(store) ||
                    cachedArtifactory.isObsolete();
            assert cachedLicense.matches(source) ||
                    cachedLicense.matches(store) ||
                    cachedLicense.isObsolete();

            // Update the association of the cached artifactory to the store
            // and invalidate the cached license because it gets shared with
            // the caller.
            this.cachedArtifactory = cachedArtifactory.source(store);
            this.cachedLicense = new CachedLicense();
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
        License license = cachedLicense.map(source);
        if (null == license)
            this.cachedLicense = new CachedLicense(source,
                    license = decodeLicense(source),
                    cachePeriodMillis());
        validation().validate(license);
        return license;
    }

    @Override
    Artifactory authenticate(final Source source) throws Exception {
        Artifactory artifactory = cachedArtifactory.map(source);
        if (null == artifactory)
            this.cachedArtifactory = new CachedArtifactory(source,
                    artifactory = super.authenticate(source),
                    cachePeriodMillis());
        return artifactory;
    }

    @Immutable
    private static class CachedArtifactory
    extends CacheEntry<Source, Artifactory> {

        private static final long serialVersionUID = 1L;

        CachedArtifactory() { this(null, null, 0); } // => isObsolete() == true

        CachedArtifactory(
                @CheckForNull Source source,
                @CheckForNull Artifactory artifactory,
                long timeoutPeriodMillis) {
            super(source, artifactory, timeoutPeriodMillis);
        }

        CachedArtifactory source(@CheckForNull Source param) {
            return new CachedArtifactory(param, getValue(), getCachePeriodMillis());
        }
    } // CachedArtifactory

    @Immutable
    private static class CachedLicense
    extends CacheEntry<Source, License> {

        private static final long serialVersionUID = 1L;

        CachedLicense() { this(null, null, 0); } // => isObsolete() == true

        CachedLicense(
                @CheckForNull Source source,
                @CheckForNull License license,
                long timeoutPeriodMillis) {
            super(source, license, timeoutPeriodMillis);
        }

        CachedLicense source(@CheckForNull Source param) {
            return new CachedLicense(param, getValue(), getCachePeriodMillis());
        }
    } // CachedLicense
}

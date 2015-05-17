/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.License;
import org.truelicense.api.LicenseManagementException;
import org.truelicense.api.codec.Decoder;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.spi.misc.Option;

import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * A basic consumer license manager which caches some computed objects to speed
 * up subsequent requests.
 * This class is thread-safe.
 *
 * @author Christian Schlichtherle
 */
class CachingTrueLicenseManager<Model>
extends TrueLicenseManager<Model> {

    // These volatile fields get initialized by applying a pure function which
    // takes the immutable value of the store() property as its single argument.
    // So some concurrent threads may safely interleave when initializing these
    // fields without creating a racing condition and thus it's not generally
    // required to synchronize access to them.
    private volatile Cache<Source, Decoder> cachedDecoder = new Cache<>();
    private volatile Cache<Source, License> cachedLicense = new Cache<>();

    CachingTrueLicenseManager(
            TrueLicenseApplicationContext<Model>.TrueLicenseManagementContext.TrueLicenseManagementParameters parameters) {
        super(parameters);
    }

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
            assert cachedDecoder.hasKey(optSource) ||
                    cachedDecoder.hasKey(optStore) ||
                    cachedDecoder.obsolete();
            assert cachedLicense.hasKey(optSource) ||
                    cachedLicense.hasKey(optStore) ||
                    cachedLicense.obsolete();

            // Update the association of the cached artifactory and license to
            // the store.
            cachedDecoder = cachedDecoder.key(optStore);
            cachedLicense = cachedLicense.key(optStore);
        }
    }

    @Override
    public void uninstall() throws LicenseManagementException {
        final Cache<Source, Decoder> cachedDecoder = new Cache<>();
        final Cache<Source, License> cachedLicense = new Cache<>();
        synchronized (store()) {
            super.uninstall();
            this.cachedDecoder = cachedDecoder;
            this.cachedLicense = cachedLicense;
        }
    }

    @Override
    void validate(final Source source) throws Exception {
        final List<Source> optSource = Option.wrap(source);
        List<License> optLicense = cachedLicense.map(optSource);
        if (optLicense.isEmpty()) {
            optLicense = Option.wrap(decodeLicense(source));
            cachedLicense = new Cache<>(optSource, optLicense, cachePeriodMillis());
        }
        validation().validate(optLicense.get(0));
    }

    @Override
    Decoder authenticate(final Source source) throws Exception {
        final List<Source> optSource = Option.wrap(source);
        List<Decoder> optDecoder = cachedDecoder.map(optSource);
        if (optDecoder.isEmpty()) {
            optDecoder = Option.wrap(super.authenticate(source));
            cachedDecoder = new Cache<>(optSource, optDecoder, cachePeriodMillis());
        }
        return optDecoder.get(0);
    }

    final long cachePeriodMillis() {
        return requireNonNegative(parameters().cachePeriodMillis());
    }

    private static long requireNonNegative(final long l) {
        if (0 > l)
            throw new IllegalArgumentException();
        return l;
    }
}

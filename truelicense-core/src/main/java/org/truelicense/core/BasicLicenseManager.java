/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.Artifactory;
import org.truelicense.api.auth.Authentication;
import org.truelicense.api.auth.Repository;
import org.truelicense.api.auth.RepositoryProvider;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.*;
import org.truelicense.spi.codec.Codecs;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Callable;

/**
 * A basic license manager.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
@Immutable
abstract class BasicLicenseManager
implements BiosProvider, LicenseParametersProvider {

    public License create(final License bean, final Sink sink)
    throws LicenseManagementException {
        return wrap(new Callable<License>() {
            @Override public License call() throws Exception {
                authorization().clearCreate(parameters());
                return encrypt(bean, sink);
            }
        });
    }

    public void install(final Source source)
    throws LicenseManagementException {
        wrap(new Callable<Void>() {
            @Override public Void call() throws Exception {
                authorization().clearInstall(parameters());
                decodeLicense(source); // checks digital signature
                bios().copy(source, store());
                return null;
            }
        });
    }

    public License view() throws LicenseManagementException {
        return wrap(new Callable<License>() {
            @Override public License call() throws Exception {
                authorization().clearView(parameters());
                return decodeLicense(store());
            }
        });
    }

    public void verify() throws LicenseManagementException {
        wrap(new Callable<Void>() {
            @Override public Void call() throws Exception {
                authorization().clearVerify(parameters());
                validate(store());
                return null;
            }
        });
    }

    public void uninstall() throws LicenseManagementException {
        wrap(new Callable<Void>() {
            @Override public Void call() throws Exception {
                authorization().clearUninstall(parameters());
                final Store store = store();
                // #TRUELICENSE-81: A license consumer manager must
                // authenticate the installed license key before uninstalling
                // it.
                authenticate(store);
                store.delete();
                return null;
            }
        });
    }

    //
    // Utility functions:
    //

    private static <V> V wrap(final Callable<V> task)
    throws LicenseManagementException {
        try { return task.call(); }
        catch (RuntimeException | LicenseManagementException ex) { throw ex; }
        catch (Throwable ex) { throw new LicenseManagementException(ex); }
    }

    //
    // License vendor functions:
    //

    License encrypt(final License bean, final Sink sink) throws Exception {
        return compress(bean, encryption().apply(sink));
    }

    License compress(final License bean, final Sink sink) throws Exception {
        return encodeRepository(bean, compression().apply(sink));
    }

    License encodeRepository(final License bean, final Sink sink)
    throws Exception {
        final LicenseAndRepositoryProvider larp = encodeAndSign(bean);
        codec().encode(sink, larp.repository());
        return larp.license();
    }

    LicenseAndRepositoryProvider encodeAndSign(final License bean)
    throws Exception {
        final License duplicate = validate(bean);
        final Repository repository = repository();
        authentication().sign(codec(), repository, duplicate);
        return new LicenseAndRepositoryProvider() {
            @Override public License license() { return duplicate; }
            @Override public Repository repository() { return repository; }
        };
    }

    License validate(final License bean) throws Exception {
        final License duplicate = initialize(bean);
        validation().validate(duplicate);
        return duplicate;
    }

    License initialize(final License bean) throws Exception {
        final License duplicate = duplicate(bean);
        initialization().initialize(duplicate);
        return duplicate;
    }

    <V> V duplicate(V object) throws Exception {
        return Codecs.clone(object, codec());
    }

    private interface LicenseAndRepositoryProvider
    extends LicenseProvider, RepositoryProvider { }

    //
    // License consumer functions:
    //

    License validate(final Source source) throws Exception {
        final License license = decodeLicense(source);
        validation().validate(license);
        return license;
    }

    License decodeLicense(Source source) throws Exception {
        return authenticate(source).decode(License.class);
    }

    Artifactory authenticate(Source source) throws Exception {
        return authentication().verify(codec(), decodeRepository(source));
    }

    Repository decodeRepository(Source source) throws Exception {
        return codec().decode(decompress(source), Repository.class);
    }

    Source decompress(Source source) {
        return compression().unapply(decrypt(source));
    }

    Source decrypt(Source source) { return encryption().unapply(source); }

    //
    // Property/factory functions:
    //

    final LicenseAuthorization authorization() {
        return parameters().authorization();
    }

    final LicenseInitialization initialization() {
        return parameters().initialization();
    }

    final LicenseValidation validation() { return parameters().validation(); }

    final Repository repository() { return parameters().repository(); }

    final Authentication authentication() {
        return parameters().authentication();
    }

    final Codec codec() { return parameters().codec(); }

    final Transformation compression() { return parameters().compression(); }

    final Encryption encryption() { return parameters().encryption(); }

    /**
     * Returns the store for the license key (optional operation).
     *
     * @throws UnsupportedOperationException If this method is called on a
     *         {@link LicenseVendorManager}.
     */
    public abstract Store store();
}

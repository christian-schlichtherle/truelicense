/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.api.*;
import org.truelicense.api.auth.*;
import org.truelicense.api.codec.Codec;
import org.truelicense.api.io.*;
import org.truelicense.spi.codec.Codecs;

import java.util.concurrent.Callable;

/**
 * A basic license manager.
 * This class is immutable.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
abstract class TrueLicenseManager<Model>
implements BiosProvider,
        LicenseParametersProvider,
        RepositoryContextProvider<Model> {

    public LicenseKeyGenerator generator(final License bean) throws LicenseManagementException {
        return wrap(new Callable<LicenseKeyGenerator>() {
            @Override
            public LicenseKeyGenerator call() throws Exception {
                authorization().clearGenerator(parameters());
                return new LicenseKeyGenerator() {

                    final RepositoryContext<Model> context = repositoryContext();
                    final Model model = context.model();
                    final Artifactory artifactory = authentication()
                            .sign(context.controller(model, codec()), validatedBean());

                    License validatedBean() throws Exception {
                        final License duplicate = initializedBean();
                        validation().validate(duplicate);
                        return duplicate;
                    }

                    License initializedBean() throws Exception {
                        final License duplicate = duplicatedBean();
                        initialization().initialize(duplicate);
                        return duplicate;
                    }

                    License duplicatedBean() throws Exception {
                        return Codecs.clone(bean, codec());
                    }

                    @Override
                    public License license() throws LicenseManagementException {
                        return wrap(new Callable<License>() {
                            @Override
                            public License call() throws Exception {
                                return artifactory.decode(License.class);
                            }
                        });
                    }

                    @Override
                    public LicenseKeyGenerator writeTo(final Sink sink) throws LicenseManagementException {
                        wrap(new Callable<Void>() {

                            @Override public Void call() throws Exception {
                                codec().to(compressedAndEncryptedSink()).encode(model);
                                return null;
                            }

                            Sink compressedAndEncryptedSink() {
                                return compression().apply(encryptedSink());
                            }

                            Sink encryptedSink() {
                                return encryption().apply(sink);
                            }
                        });
                        return this;
                    }
                };
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

    /**
     * Executes the given {@code task} and wraps any
     * non-{@link RuntimeException} and non-{@link LicenseManagementException}
     * in a new {@code LicenseManagementException}.
     *
     * @param  task the task to {@link Callable#call}.
     * @return the result of calling the task.
     * @throws RuntimeException at the discretion of the task.
     * @throws LicenseManagementException on any other {@link Exception} thrown
     *         by the task.
     */
    static <V> V wrap(final Callable<V> task)
    throws LicenseManagementException {
        try { return task.call(); }
        catch (RuntimeException | LicenseManagementException e) { throw e; }
        catch (Exception e) { throw new LicenseManagementException(e); }
    }

    //
    // License consumer functions:
    //

    void validate(Source source) throws Exception {
        validation().validate(decodeLicense(source));
    }

    License decodeLicense(Source source) throws Exception {
        return authenticate(source).decode(License.class);
    }

    Artifactory authenticate(Source source) throws Exception {
        return authentication().verify(repositoryController(source));
    }

    RepositoryController repositoryController(Source source) throws Exception {
        return repositoryContext().controller(repositoryModel(source), codec());
    }

    Model repositoryModel(Source source) throws Exception {
        return codec().from(decompress(source)).decode(repositoryContext().model().getClass());
    }

    Source decompress(Source source) {
        return compression().unapply(decrypt(source));
    }

    Source decrypt(Source source) { return encryption().unapply(source); }

    //
    // Property/factory functions:
    //

    final Authentication authentication() {
        return parameters().authentication();
    }

    final LicenseAuthorization authorization() {
        return parameters().authorization();
    }

    @Override
    public final BIOS bios() { return parameters().bios(); }

    final Codec codec() { return parameters().codec(); }

    final Transformation compression() { return parameters().compression(); }

    final Transformation encryption() { return parameters().encryption(); }

    final LicenseInitialization initialization() {
        return parameters().initialization();
    }

    @Override
    public abstract TrueLicenseApplicationContext<?, Model>.TrueLicenseParameters parameters();

    @Override
    public final RepositoryContext<Model> repositoryContext() {
        return parameters().repositoryContext();
    }

    /**
     * Returns the store for the license key (optional operation).
     *
     * @throws UnsupportedOperationException If this method is called on a
     *         {@link LicenseVendorManager}.
     */
    public abstract Store store();

    final LicenseValidation validation() { return parameters().validation(); }
}

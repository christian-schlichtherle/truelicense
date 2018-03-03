/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Store;
import net.truelicense.api.codec.CodecProvider;
import net.truelicense.api.misc.ClassLoaderProvider;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * A context for license management.
 * Use this context to configure and build a
 * {@linkplain #vendor() vendor license manager} or a
 * {@linkplain #consumer() consumer license manager}.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementContext
extends ClassLoaderProvider,
        CodecProvider,
        LicenseFactory,
        LicenseManagementSubjectProvider {

    /**
     * Returns a builder for a
     * {@linkplain ConsumerLicenseManager consumer license manager}.
     * Call its {@link ConsumerLicenseManagerBuilder#build} method to build
     * the configured consumer license manager.
     */
    ConsumerLicenseManagerBuilder consumer();

    /** Returns a new memory store. */
    Store memoryStore(); // FIXME: Remove this!

    /** Returns a store for the given path. */
    Store pathStore(Path path); // FIXME: Remove this!

    /**
     * Returns an input stream socket which loads the resource with the given {@code name}.
     * The provided string should be computed on demand from an obfuscated form,
     * e.g. by processing it with the TrueLicense Maven Plugin.
     * <p>
     * The resource will get loaded using the class loader which is returned by
     * the method {@link #classLoader()}.
     *
     * @param  name the name of the resource to load.
     * @return A source which loads the resource with the given {@code name}.
     */
    Socket<InputStream> resource(String name); // FIXME: Remove this!

    /** Returns an input stream socket which reads from standard input without ever closing it. */
    Socket<InputStream> stdin(); // FIXME: Remove this!

    /** Returns an output stream socket which writes to standard output without ever closing it. */
    Socket<OutputStream> stdout(); // FIXME: Remove this!

    /**
     * Returns a store for the system preferences node for the package of the
     * given class.
     * Note that the class should be excluded from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     * <p>
     * The store will use the {@linkplain #subject() license subject} as its
     * key in the preferences node.
     */
    Store systemPreferencesStore(Class<?> classInPackage); // FIXME: Remove this!

    /**
     * Returns a store for the user preferences node for the package of the
     * given class.
     * Note that the class should be excluded from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     * <p>
     * The store will use the {@linkplain #subject() license subject} as its
     * key in the preferences node.
     */
    Store userPreferencesStore(Class<?> classInPackage); // FIXME: Remove this!

    /**
     * Returns a builder for a
     * {@linkplain VendorLicenseManager vendor license manager}.
     * Call its {@link VendorLicenseManagerBuilder#build} method to build
     * the configured vendor license manager.
     */
    VendorLicenseManagerBuilder vendor();
}

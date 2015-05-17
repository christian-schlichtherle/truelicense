/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;

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
extends CodecProvider,
        LicenseFactory,
        LicenseSubjectProvider {

    /**
     * Returns a builder for a
     * {@linkplain ConsumerLicenseManager consumer license manager}.
     * Call its {@link ConsumerLicenseManagerBuilder#build} method to build
     * the configured consumer license manager.
     */
    ConsumerLicenseManagerBuilder consumer();

    /** Returns a new memory store. */
    Store memoryStore();

    /** Returns a store for the given path. */
    Store pathStore(Path path);

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * The provided string should be computed on demand from an obfuscated form,
     * e.g. by processing it with the TrueLicense Maven Plugin.
     * <p>
     * The resource will get loaded using the class loader as defined by the
     * root license management context.
     *
     * @param  name the name of the resource to load.
     * @return A source which loads the resource with the given {@code name}.
     */
    Source resource(String name);

    /**
     * Returns a source which reads from standard input without ever closing it.
     */
    Source stdin();

    /**
     * Returns a sink which writes to standard output without ever closing it.
     */
    Sink stdout();

    /**
     * Returns a store for the system preferences node for the package of the
     * given class.
     * Note that the class should be excluded from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     */
    Store systemPreferencesStore(Class<?> classInPackage);

    /**
     * Returns a store for the user preferences node for the package of the
     * given class.
     * Note that the class should be excluded from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     */
    Store userPreferencesStore(Class<?> classInPackage);

    /**
     * Returns a builder for a
     * {@linkplain VendorLicenseManager vendor license manager}.
     * Call its {@link VendorLicenseManagerBuilder#build} method to build
     * the configured vendor license manager.
     */
    VendorLicenseManagerBuilder vendor();
}

/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;

import java.nio.file.Path;

/**
 * A context which has been derived from a
 * {@linkplain LicenseManagementContext license management context}.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseApplicationContext {

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
     * Returns a store for the system preferences node for the package of the
     * given class.
     * Note that the class should be exempt from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     */
    Store systemPreferencesStore(Class<?> classInPackage);

    /**
     * Returns a store for the user preferences node for the package of the
     * given class.
     * Note that the class should be exempt from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     */
    Store userPreferencesStore(Class<?> classInPackage);
}

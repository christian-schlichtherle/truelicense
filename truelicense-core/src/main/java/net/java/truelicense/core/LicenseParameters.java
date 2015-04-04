/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import net.java.truelicense.core.auth.*;
import net.java.truelicense.core.codec.CodecProvider;
import net.java.truelicense.core.comp.CompressionProvider;
import net.java.truelicense.core.crypto.EncryptionProvider;

/**
 * Defines license parameters.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @see    LicenseParametersProvider
 * @author Christian Schlichtherle
 */
public interface LicenseParameters
extends AuthenticationProvider,
        CodecProvider,
        CompressionProvider,
        EncryptionProvider,
        LicenseAuthorizationProvider,
        LicenseInitializationProvider,
        LicenseValidationProvider,
        RepositoryProvider {

    /** Returns a <em>new</em> repository. */
    @Override
    Repository repository();
}

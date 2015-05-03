/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.AuthenticationProvider;
import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.comp.CompressionProvider;
import org.truelicense.api.crypto.EncryptionProvider;
import org.truelicense.api.io.BiosProvider;

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
        CodecProvider, // TODO: Remove this!
        CompressionProvider, // TODO: Remove this!
        EncryptionProvider,
        LicenseAuthorizationProvider,
        LicenseInitializationProvider,
        LicenseValidationProvider {
}

package org.truelicense.api;

import org.truelicense.api.misc.ContextProvider;

/**
 * Provides a {@linkplain LicenseManagementContext license management context}
 * and {@link LicenseManagementParameters license management parameters}.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementSchema
extends ContextProvider<LicenseManagementContext<?>>,
        LicenseManagementParametersProvider {
}

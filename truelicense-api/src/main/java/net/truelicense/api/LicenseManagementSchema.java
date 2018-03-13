/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.misc.ContextProvider;

/**
 * Provides a {@linkplain LicenseManagementContext license management context}
 * and {@link LicenseManagementParameters license management parameters}.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementSchema
extends ContextProvider<LicenseManagementContext>, LicenseManagementParametersProvider { }

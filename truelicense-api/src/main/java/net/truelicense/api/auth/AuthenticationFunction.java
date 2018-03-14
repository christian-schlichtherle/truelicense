/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

import java.util.function.Function;

/**
 * Maps authentication parameters to authentications.
 *
 * @author Christian Schlichtherle
 */
public interface AuthenticationFunction extends Function<AuthenticationParameters, Authentication> { }

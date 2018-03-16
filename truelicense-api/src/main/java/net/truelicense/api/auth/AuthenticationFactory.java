/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

import java.util.function.Function;

/**
 * Creates an authentication from some parameters.
 *
 * @author Christian Schlichtherle
 */
public interface AuthenticationFactory extends Function<AuthenticationParameters, Authentication> { }

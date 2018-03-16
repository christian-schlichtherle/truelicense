/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

import global.namespace.fun.io.api.Transformation;

import java.util.function.Function;

/**
 * Creates a password based encryption transformation from some parameters.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionFactory extends Function<EncryptionParameters, Transformation> { }

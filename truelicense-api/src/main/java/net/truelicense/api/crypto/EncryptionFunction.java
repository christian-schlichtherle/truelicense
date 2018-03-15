/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

import global.namespace.fun.io.api.Transformation;

import java.util.function.Function;

/**
 * Maps (password based) encryption parameters to encryption transformations.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionFunction extends Function<EncryptionParameters, Transformation> { }

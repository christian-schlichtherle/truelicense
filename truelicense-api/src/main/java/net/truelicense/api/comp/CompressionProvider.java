/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.comp;

import global.namespace.fun.io.api.Transformation;

/**
 * Provides a compression.
 *
 * @author Christian Schlichtherle
 */
public interface CompressionProvider {

    /** Returns a compression transformation. */
    Transformation compression();
}

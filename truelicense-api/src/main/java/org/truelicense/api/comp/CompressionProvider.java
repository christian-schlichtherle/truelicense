/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.comp;

import org.truelicense.api.io.Transformation;

/**
 * Provides a compression.
 *
 * @author Christian Schlichtherle
 */
public interface CompressionProvider {

    /** Returns a compression. */
    Transformation compression();
}

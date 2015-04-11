/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.comp;

import org.truelicense.api.io.Transformation;

/**
 * Provides a compression transformation.
 *
 * @author Christian Schlichtherle
 */
public interface CompressionProvider {
    /** Returns the compression transformation. */
    Transformation compression();
}

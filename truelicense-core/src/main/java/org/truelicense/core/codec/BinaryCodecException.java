/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.codec;

import org.truelicense.api.codec.Codec;

import javax.annotation.CheckForNull;

/**
 * Indicates that a {@link Codec} produces binary output instead of the
 * expected text output.
 *
 * @author Christian Schlichtherle
 */
public class BinaryCodecException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public BinaryCodecException() { }

    public BinaryCodecException(@CheckForNull String message) {
        super(message);
    }
}

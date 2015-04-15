/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.codec;

import org.truelicense.api.codec.Codec;

import javax.annotation.Nullable;

/**
 * Indicates that a {@link Codec} produces binary output instead of the
 * expected text output.
 *
 * @author Christian Schlichtherle
 */
public class BinaryCodecException extends IllegalArgumentException {

    private static final long serialVersionUID = 0L;

    public BinaryCodecException() { }

    public BinaryCodecException(@Nullable String message) {
        super(message);
    }
}

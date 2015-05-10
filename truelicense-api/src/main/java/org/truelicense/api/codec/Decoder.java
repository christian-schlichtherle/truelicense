/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.codec;

import java.lang.reflect.Type;

/**
 * Decodes an object graph.
 *
 * @author Christian Schlichtherle
 */
public interface Decoder {

    /**
     * Decodes an object graph of the expected type.
     *
     * @param  <T>
     *         the expected type of the decoded object.
     * @param  expected
     *         the expected type of the decoded object graph, e.g.
     *         {@code String.class}.
     *         This is just a hint and the implementation may ignore it.
     * @return A duplicate of the original object graph.
     *         Its actual type may differ from the expected type.
     */
    <T> T decode(Type expected) throws Exception;
}

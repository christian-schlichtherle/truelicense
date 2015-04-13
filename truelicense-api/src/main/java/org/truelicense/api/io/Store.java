/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.io;

import java.io.IOException;

/**
 * An abstraction for storing data.
 *
 * @author Christian Schlichtherle
 */
public interface Store extends Source, Sink {

    /** A reasonable buffer size, which is {@value}. */
    int BUFSIZE = 8 * 1024;

    /** Deletes the data. */
    void delete() throws IOException;

    /** Returns {@code true} if and only if the data exists. */
    boolean exists();
}
